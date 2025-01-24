package me.blackjack.game.impl.game

import me.blackjack.bank.BankService
import me.blackjack.game.impl.game.sidebet.BustSideBet
import me.blackjack.game.impl.game.sidebet.InsuranceSideBet
import me.blackjack.game.impl.game.sidebet.LuckyLuckSideBet
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
) : GameHands(deck, bankService, ruleService) {

    var state: State = State.PREPARE
        private set

    override var bet = previousBet
        private set

    val sideBets = mutableListOf<SideBet>()

    val payouts = mutableListOf<GamePayout>()
    
    fun input(input: PlayerInput): Boolean = when (input) {
        is Initiate -> throw IllegalStateException("Initiate must be first input")

        is PregameSideBet -> {
            assert(state == State.PREPARE)

            bankService.reserve(input.bet)

            when (input.type) {
                PregameSideBet.Type.BUST -> sideBets.add(BustSideBet(this, input.bet, ruleService))
                PregameSideBet.Type.LUCKY_LUCKY -> sideBets.add(LuckyLuckSideBet(this, input.bet, ruleService))
            }

            false
        }

        is SetBet -> {
            assert(state == State.PREPARE)

            bet = input.bet
            false
        }

        Start -> {
            assert(state == State.PREPARE)

            bankService.reserve(bet)
            
            playerHands.first().hit()
            dealerHand.hit()
            playerHands.first().hit()

            if (ruleService.getValue(Rule.Dealer.peeks)) {
                dealerHand.hit()

                if (dealerHand.isBlackjack && ruleService.getValue(Rule.Dealer.checksBlackjack)) {
                    state = State.FINISHED
                }
            }

            state = if (playerHands.first().isBlackjack) State.DEALER else State.PLAYER

            sideBets.forEach { it.startGame() }
            false
        }
        
        is Insure -> {
            assert(state == State.PLAYER && playerHands.size == 1 && currentHand.isUnplayed())

            bankService.reserve(input.bet)
            sideBets.add(InsuranceSideBet(this, input.bet, ruleService))
            false
        }
        
        Hit -> {
            assert(state == State.PLAYER && currentHand.canHit())
            
            currentHand.hit()
            
            if (!currentHand.canHit()) {
                if (hasNextHand()) {
                    nextHand()
                } else {
                    state = State.DEALER
                }
            }

            false
        }
        
        Stand -> {
            assert(state == State.PLAYER && currentHand.canHit())
            
            if (hasNextHand()) nextHand()
            else state = State.DEALER

            false
        }
        
        Double -> {
            assert(state == State.PLAYER && currentHand.canDouble())
            
            currentHand.double()
            
            if (hasNextHand()) nextHand()
            else state = State.DEALER

            false
        }
        
        Split -> {
            assert(state == State.PLAYER && currentHand.canSplit())
            
            currentHand.split()
            
            if (!currentHand.canHit()) {
                if (hasNextHand()) nextHand()
                else state = State.DEALER
            }

            false
        }
        
        SurrenderCurrent -> {
            assert(state == State.PLAYER && currentHand.canSurrender())
            
            currentHand.surrender()
            
            if (hasNextHand()) nextHand()
            else state = State.DEALER

            false
        }
        
        Proceed -> run {
            assert(state == State.DEALER)
            
            if (dealerSecondCardHidden) {
                dealerSecondCardHidden = false

                if (dealerHand.size == 1) dealerHand.hit()

                if (dealerHand.isBlackjack) {
                    state = State.FINISHED
                    return@run false
                }

                if (mustDealerHit()) return@run false
                else {
                    state = State.FINISHED
                    return@run false
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
                state = State.FINISHED
                return@run false
            }
            
            dealerHand.hit()
            
            if (dealerHand.isBlackjack) state = State.FINISHED
            else if (dealerHand.isBust) state = State.FINISHED
            else if (!mustDealerHit()) state = State.FINISHED

            false
        }
        
        is Surrender -> {
            assert(state == State.DEALER && playerHands[input.hand].canSurrender() )

            playerHands[input.hand].surrender()

            if (playerHands.all {
                    it.isBlackjack ||
                            it.isBust ||
                            it.surrendered ||
                            it.isTrippleSeven() ||
                            it.isFiveCard()
                }) {
                state = State.FINISHED
            }

            false
        }

        Payout -> {
            assert(state == State.FINISHED && payouts.isEmpty())
            payoutHands()
            payoutSideBets()
            false
        }

        FinishGame -> {
            assert(state == State.FINISHED && payouts.isNotEmpty())
            true
        }

        AbortGame -> {
            if (state == State.FINISHED) {
                payoutHands()
                payoutSideBets()
            } else {
                sideBets.forEach { bankService.free(it.bet) }
                playerHands.forEach { bankService.free(bet * if (it.doubled) 2 else 1) }
            }


            true
        }
    }
    
    fun getPossibleActions(): List<KClass<out PlayerInput>> = when(state) {
        State.UNINITIALIZED -> throw IllegalStateException("Game not initialized")

        State.PREPARE -> listOf(
                PregameSideBet::class,
                SetBet::class,
                Start::class,
            )
        
        State.PLAYER -> {
            val actions = mutableListOf<KClass<out PlayerInput>>()

            if (currentHand.canHit()) {
                actions.add(Hit::class)
                actions.add(Stand::class)
            }

            if (playerHands.size == 1 && currentHand.isUnplayed())
                actions.add(Insure::class)
            
            if (currentHand.canDouble()) actions.add(Double::class)
            if (currentHand.canSplit()) actions.add(Split::class)
            if (currentHand.canSurrender()) actions.add(SurrenderCurrent::class)
            
            actions
        }
        
        State.DEALER -> buildList {
            add(Proceed::class)
            if (playerHands.any { it.canSurrender() }) add(Surrender::class)
        }
        
        State.FINISHED -> buildList {
            if (payouts.isEmpty()) add(Payout::class)
            else add(FinishGame::class)
        }
    }

    private fun payoutHands() {
        playerHands.forEachIndexed { index, it ->
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

            val amount = (bet * payout * multipler).toLong()

            payouts.add(HandPayout(index, amount))
            bankService.add(amount)
        }
    }

    private fun payoutSideBets() {
        sideBets.forEachIndexed { index, it ->
            if (it.state == SideBet.State.PENDING) throw IllegalStateException("Side bet not resolved")

            bankService.free(it.bet)

            val amount = if (it.state == SideBet.State.LOST) -it.bet
            else (it.bet * it.payout).toLong()

            payouts.add(SidebetPayout(index, amount))
            bankService.add(amount)
        }
    }

    sealed interface GamePayout {

        val amount: Long

    }

    data class HandPayout(val hand: Int, override val amount: Long) : GamePayout

    data class SidebetPayout(val bet: Int, override val amount: Long): GamePayout

    enum class State {

        UNINITIALIZED,
        PREPARE,
        PLAYER,
        DEALER,
        FINISHED,

    }

}