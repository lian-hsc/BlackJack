package me.blackjack.game.impl.game

import me.blackjack.bank.BankService
import me.blackjack.game.impl.model.Card
import me.blackjack.game.impl.model.Deck
import me.blackjack.game.impl.model.Hand
import me.blackjack.game.impl.model.value
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService

internal sealed class GameHands(
    private val deck: Deck,
    private val bankService: BankService,
    private val ruleService: RuleService,
    private val bet: Long,
) {

    val dealerHand = Hand()
    val playerHands = mutableListOf(Hand())

    var currentHandIndex = 0

    val currentHand
        get() = playerHands[currentHandIndex]

    var dealerSecondCardHidden = true
        protected set

    init {
        if (deck.shouldShuffle) throw IllegalStateException("Deck should be shuffled")
        if (bankService.availableCapital < bet) throw IllegalStateException("Bank is empty")
    }

    protected fun mustDealerHit() =
        dealerHand.value < 17 || (dealerHand.value == 17 && isDealerSoft() && ruleService.getValue(Rule.Dealer.hitOnSoft17))

    private fun isDealerSoft() = dealerHand.any { it.rank == Card.CardRank.ACE }

    protected fun Hand.isUnplayed(): Boolean {
        if (size != 2) return false
        if (splits > 0) return false
        if (doubled) return false
        if (surrendered) return false

        return true
    }

    protected fun Hand.isTrippleSeven(): Boolean {
        if (size != 3) return false
        if (any { it.rank != Card.CardRank.SEVEN }) return false
        if (!ruleService.getValue(Rule.TrippleSeven.enabled)) return false

        return true
    }

    protected fun Hand.isFiveCard(): Boolean {
        if (size != 5) return false
        if (!ruleService.getValue(Rule.FiveCards.enabled)) return false

        return true
    }

    protected fun Hand.canSplit(): Boolean {
        if (size != 2) return false
        if (get(0).rank != get(1).rank) return false

        if (get(0).rank != Card.CardRank.ACE && ruleService.getValue(Rule.Split.amount)
                ?.let { splits >= it } == true
        ) return false
        if (get(0).rank == Card.CardRank.ACE && ruleService.getValue(Rule.Split.acesAmount)
                ?.let { splits >= it } == true
        ) return false

        if (bankService.availableCapital < bet) return false

        return true
    }

    protected fun Hand.canDouble(): Boolean {
        if (size != 2) return false
        if (splits > 0 && !ruleService.getValue(Rule.DoubleDown.afterSplit)) return false
        if (ruleService.getValue(Rule.DoubleDown.onlyOnSoft) && value !in 9..11) return false

        if (bankService.availableCapital < bet) return false

        return true
    }

    protected fun Hand.canHit(): Boolean {
        if (doubled || surrendered || isBust) return false
        if (value == 21) return false

        if (splits >= 1 && get(0).rank == Card.CardRank.ACE && !ruleService.getValue(Rule.Split.newCardsAfterAce)) return false
        if (size == 5 && ruleService.getValue(Rule.FiveCards.enabled)) return false

        return true
    }

    protected fun Hand.canSurrender(): Boolean {
        if (splits > 0 && !ruleService.getValue(Rule.Surrender.afterSplit)) return false
        if (doubled && !ruleService.getValue(Rule.DoubleDown.rescue)) return false
        if (dealerSecondCardHidden && !ruleService.getValue(Rule.Surrender.early)) return false
        if (!dealerSecondCardHidden && !ruleService.getValue(Rule.Surrender.late)) return false
        if (dealerHand[0].rank == Card.CardRank.ACE && !ruleService.getValue(Rule.Surrender.againstAce)) return false

        return true
    }

    fun canHandSurrender(hand: Hand) = hand.canSurrender()

    protected fun Hand.split() {
        if (!canSplit()) throw IllegalStateException("Cannot split hand")

        val newHand = Hand(mutableListOf(removeAt(1)), splits + 1)
        playerHands.add(currentHandIndex + 1, newHand)

        splits++

        hit()
        newHand.hit()

        bankService.reserve(bet)
    }

    protected fun Hand.double() {
        if (!canDouble()) throw IllegalStateException("Cannot double hand")

        doubled = true
        hit()

        bankService.reserve(bet)
    }

    protected fun Hand.hit() = add(deck.draw())

    protected fun Hand.surrender() {
        if (!canSurrender()) throw IllegalStateException("Cannot surrender hand")

        surrendered = true
    }

    protected fun hasNextHand(): Boolean = currentHandIndex < playerHands.size - 1

    protected fun nextHand() {
        if (!hasNextHand()) throw IllegalStateException("No next hand")

        currentHandIndex++
    }

}