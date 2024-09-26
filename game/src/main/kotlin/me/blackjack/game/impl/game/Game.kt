package me.blackjack.game.impl.game

import me.blackjack.bank.BankService
import me.blackjack.game.impl.game.sidebet.BustSideBet
import me.blackjack.game.impl.game.sidebet.InsuranceSideBet
import me.blackjack.game.impl.game.sidebet.SideBet
import me.blackjack.game.impl.model.Deck
import me.blackjack.game.impl.model.value
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService
import kotlin.reflect.KClass

internal class Game(
    deck: Deck,
    private val bankService: BankService,
    private val ruleService: RuleService,
    previousBet: Long,
) : GameHands(deck, bankService, ruleService, previousBet) {

    var state: Sate = Sate.PREPARE
        private set

    var bet = previousBet
        private set

    val sideBets = mutableListOf<SideBet>()
    
    fun input(input: PlayerInput): GameReaction = when (input) {
        is PregameSideBet -> {
            assert(state == Sate.PREPARE)

            bankService.reserve(input.bet)

            when (input.type) {
                PregameSideBet.Type.BUST -> sideBets.add(BustSideBet(this, input.bet, ruleService))
                PregameSideBet.Type.LUCKY_LUCKY -> sideBets.add(BustSideBet(this, input.bet, ruleService))
            }

            AwaitInput
        }

        is SetBet -> {
            assert(state == Sate.PREPARE)

            bet = input.bet
            AwaitInput
        }

        Start -> run {
            assert(state == Sate.PREPARE)

            bankService.reserve(bet)
            
            playerHands.first().hit()
            dealerHand.hit()
            playerHands.first().hit()

            if (ruleService.getValue(Rule.Dealer.peeks)) {
                dealerHand.hit()

                if (dealerHand.isBlackjack && ruleService.getValue(Rule.Dealer.checksBlackjack)) {
                    state = Sate.FINISHED
                    return@run EndGame.Reason.DEALER_PEEKED_BLACKJACK()
                }
            }
            
            state = Sate.PLAYER
            AwaitInput
        }
        
        is Insure -> {
            assert(state == Sate.PLAYER && playerHands.size == 1 && currentHand.isUnplayed())

            bankService.reserve(input.bet)
            sideBets.add(InsuranceSideBet(this, input.bet, ruleService))
            AwaitInput
        }
        
        Hit -> {
            assert(state == Sate.PLAYER && currentHand.canHit())
            
            currentHand.hit()
            
            if (!currentHand.canHit()) {
                if (hasNextHand()) {
                    nextHand()
                    AwaitInput
                } else {
                    state = Sate.DEALER
                    StartDealer
                }
            } else AwaitInput
        }
        
        Stand -> {
            assert(state == Sate.PLAYER && currentHand.canHit())
            
            if (hasNextHand()) {
                nextHand()
                AwaitInput
            } else {
                state = Sate.DEALER
                StartDealer
            }
        }
        
        Double -> {
            assert(state == Sate.PLAYER && currentHand.canDouble())
            
            currentHand.double()
            
            if (hasNextHand()) {
                nextHand()
                AwaitInput
            } else {
                state = Sate.DEALER
                StartDealer
            }
        }
        
        Split -> {
            assert(state == Sate.PLAYER && currentHand.canSplit())
            
            currentHand.split()
            
            if (!currentHand.canHit()) {
                if (hasNextHand()) {
                    nextHand()
                    AwaitInput
                } else {
                    state = Sate.DEALER
                    StartDealer
                }
            } else AwaitInput
        }
        
        SurrenderCurrent -> {
            assert(state == Sate.PLAYER && currentHand.canSurrender())
            
            currentHand.surrender()
            
            if (hasNextHand()) {
                nextHand()
                AwaitInput
            } else {
                state = Sate.DEALER
                StartDealer
            }
        }
        
        Proceed -> run {
            assert(state == Sate.DEALER)
            
            if (dealerSecondCardHidden) {
                dealerSecondCardHidden = false

                if (dealerHand.size == 1) dealerHand.hit()

                if (dealerHand.isBlackjack) {
                    state = Sate.FINISHED
                    return@run EndGame.Reason.DEALER_BLACKJACK()
                }

                if (mustDealerHit()) return@run AwaitInput
                else {
                    state = Sate.FINISHED
                    EndGame.Reason.DEALER_DONE()
                }
            }

            assert(mustDealerHit())

            if (playerHands.all {
                    it.isBlackjack ||
                            it.isBust ||
                            it.surrendered ||
                            it.isTrippleSeven() ||
                            it.isFiveCard()
                }) {
                state = Sate.FINISHED
                return@run EndGame.Reason.DEALER_NO_ACTION()
            }
            
            dealerHand.hit()
            
            if (dealerHand.isBlackjack) {
                state = Sate.FINISHED
                EndGame.Reason.DEALER_BLACKJACK()
            } else if (dealerHand.isBust) {
                state = Sate.FINISHED
                EndGame.Reason.DEALER_BUST()
            } else if (mustDealerHit()) {
                AwaitInput
            } else {
                state = Sate.FINISHED
                EndGame.Reason.DEALER_DONE()
            }
        }
        
        is Surrender -> {
            assert(state == Sate.DEALER && input.hands.all { playerHands[it].canSurrender() })
            
            input.hands.forEach { playerHands[it].surrender() }

            if (playerHands.all {
                    it.isBlackjack ||
                            it.isBust ||
                            it.surrendered ||
                            it.isTrippleSeven() ||
                            it.isFiveCard()
                }) {
                state = Sate.FINISHED
                EndGame.Reason.DEALER_NO_ACTION()
            } else AwaitInput
        }

        FinishGame -> {
            assert(state == Sate.FINISHED)
            payoutHands()
            payoutSideBets()
            GameDone
        }

        AbortGame -> {
            sideBets.forEach { bankService.free(it.bet) }
            playerHands.forEach { bankService.free(bet * if (it.doubled) 2 else 1) }

            GameDone
        }
    }
    
    fun getPossibleActions(): List<KClass<out PlayerInput>> = when(state) {
        Sate.PREPARE -> listOf(
                PregameSideBet::class,
                SetBet::class,
                Start::class,
            )
        
        Sate.PLAYER -> {
            val actions = mutableListOf<KClass<out PlayerInput>>()
            
            if (playerHands.size == 1 && currentHand.isUnplayed())
                actions.add(Insure::class)
            
            if (currentHand.canHit()) {
                actions.add(Hit::class)
                actions.add(Stand::class)
            }
            
            if (currentHand.canDouble()) actions.add(Double::class)
            if (currentHand.canSplit()) actions.add(Split::class)
            if (currentHand.canSurrender()) actions.add(SurrenderCurrent::class)
            
            actions
        }
        
        Sate.DEALER -> listOf(Proceed::class, Surrender::class)
        
        Sate.FINISHED -> listOf(FinishGame::class)
    }

    private fun payoutHands() {
        playerHands.forEach {
            val multipler = if (it.doubled) 2 else 1
            bankService.free(bet * multipler)

            val payout = if (it.surrendered) -0.5
            else if (it.isBust) -1.0
            else if (it.isFiveCard())
                (ruleService.getValue(Rule.FiveCards.payout) ?: ruleService.getValue(Rule.General.blackjackPayout))
            else if (it.isTrippleSeven()) (ruleService.getValue(Rule.TrippleSeven.payout)
                ?: ruleService.getValue(Rule.General.blackjackPayout))
            else if (it.isBlackjack) {
                if (dealerHand.isBlackjack) 0.0
                else ruleService.getValue(Rule.General.blackjackPayout)
            }
            else if (dealerHand.isBust) {
                if (ruleService.getValue(Rule.General.push22)) 0.0
                else 1.0
            }
            else it.value.compareTo(dealerHand.value).toDouble()

            bankService.add((bet * payout * multipler).toLong())
        }
    }

    private fun payoutSideBets() {
        sideBets.forEach {
            if (it.state == SideBet.State.PENDING) throw IllegalStateException("Side bet not resolved")

            bankService.free(it.bet)

            if (it.state == SideBet.State.LOST) bankService.remove(it.bet)
            else bankService.add((it.bet * it.payout).toLong())
        }
    }

    enum class Sate {

        PREPARE,
        PLAYER,
        DEALER,
        FINISHED,

    }

}