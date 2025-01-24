package me.blackjack.game.impl.game.sidebet

import me.blackjack.game.impl.game.Game
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService

internal class BustSideBet(
    private val game: Game,
    override val bet: Long,
    private val ruleService: RuleService,
) : SideBet {

    override val state: SideBet.State
        get() =
            if (game.state == Game.State.FINISHED) {
                if (game.dealerHand.isBust) SideBet.State.WON
                else SideBet.State.LOST
            } else SideBet.State.PENDING

    override val payout: Double
        get() =
            if (state == SideBet.State.WON) ruleService.getValue(Rule.BustSideBet.payout)
                    ?: ruleService.getValue(Rule.General.blackjackPayout)
            else 0.0

}