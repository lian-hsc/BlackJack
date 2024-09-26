package me.blackjack.game.impl.game.sidebet

import me.blackjack.game.impl.game.Game
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService

internal class InsuranceSideBet(
    private val game: Game,
    override val bet: Long,
    private val ruleService: RuleService,
) : SideBet {

    override val state: SideBet.State
        get() =
            if (game.dealerSecondCardHidden) SideBet.State.PENDING
            else if (game.dealerHand.isBlackjack) SideBet.State.WON
            else SideBet.State.LOST

    override val payout: Double
        get() =
            if (state == SideBet.State.WON)
                ruleService.getValue(Rule.Insurance.payout)
                    ?: ruleService.getValue(Rule.General.blackjackPayout)
            else 0.0

}