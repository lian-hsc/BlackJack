package me.blackjack.game.impl.game.sidebet

import me.blackjack.game.impl.game.Game
import me.blackjack.game.impl.model.Card
import me.blackjack.game.impl.model.value
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService

internal class LuckyLuckSideBet(
    private val game: Game,
    override val bet: Long,
    private val ruleService: RuleService,
) : SideBet {

    override val state: SideBet.State
        get() =
            if (game.state == Game.Sate.PREPARE) SideBet.State.PENDING
            else if (value == 19 || value == 20 || value == 21) SideBet.State.WON
            else SideBet.State.LOST

    override val payout: Double
        get() = if (state == SideBet.State.WON) {
            when (value) {
                19 -> ruleService.getValue(Rule.LuckyLuckySideBet.payout19)
                    ?: ruleService.getValue(Rule.General.blackjackPayout)

                20 -> ruleService.getValue(Rule.LuckyLuckySideBet.payout20)
                    ?: ruleService.getValue(Rule.General.blackjackPayout)

                21 -> when {
                    isTrippe && matches -> ruleService.getValue(Rule.LuckyLuckySideBet.payoutTrippleMatch)
                        ?: ruleService.getValue(Rule.General.blackjackPayout)

                    isSequence && matches -> ruleService.getValue(Rule.LuckyLuckySideBet.payoutSequenceMatch)
                        ?: ruleService.getValue(Rule.General.blackjackPayout)

                    isTrippe -> ruleService.getValue(Rule.LuckyLuckySideBet.payoutTripple)
                        ?: ruleService.getValue(Rule.General.blackjackPayout)

                    isSequence -> ruleService.getValue(Rule.LuckyLuckySideBet.payoutSequence)
                        ?: ruleService.getValue(Rule.General.blackjackPayout)

                    matches -> ruleService.getValue(Rule.LuckyLuckySideBet.payout21Match)
                        ?: ruleService.getValue(Rule.General.blackjackPayout)

                    else ->  ruleService.getValue(Rule.LuckyLuckySideBet.payout21)
                        ?: ruleService.getValue(Rule.General.blackjackPayout)
                }

                else -> 0.0
            }
        } else 0.0

    private val cards
        get() = game.playerHands.first().take(2) + game.dealerHand.first()

    private val value
        get() = cards.value

    private val matches
        get() = cards.map { it.suit }.toSet().size == 1

    private val isTrippe
        get() = cards.all { it.rank == Card.CardRank.SEVEN }

    private val isSequence
        get() = cards
            .map { it.rank }
            .containsAll(sequenceList)

    companion object {

        private val sequenceList = listOf(Card.CardRank.SIX, Card.CardRank.SEVEN, Card.CardRank.EIGHT)

    }

}