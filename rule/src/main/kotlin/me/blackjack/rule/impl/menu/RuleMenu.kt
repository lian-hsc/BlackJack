package me.blackjack.rule.impl.menu

import me.blackjack.menu.InputReaction
import me.blackjack.menu.Redraw
import me.blackjack.rule.Rule
import me.blackjack.rule.impl.RuleService
import me.blackjack.rule.impl.stored.StoredRuleService
import me.blackjack.terminal.*
import org.koin.core.annotation.Single
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import me.blackjack.rule.menu.RuleMenu as IRuleMenu

@Single
internal class RuleMenu(
    val ruleService: RuleService,
    val storedRuleService: StoredRuleService,
) : IRuleMenu {

    private var currentSubmenu: Submenu? = null

    private var selectedSubmenu: Submenu? = Submenu.GENERAL

    private var selectedRule: Rule<*>? = null

    override fun onPush() {
        currentSubmenu = null
        selectedSubmenu = Submenu.GENERAL
    }

    override fun getState(): List<String> =
        if (currentSubmenu == null) {
            Submenu.entries.map {
                if (it == selectedSubmenu) it.title.rgb(255, 255, 85)
                else it.title
            }
        } else {
            currentSubmenu!!.rules.map {
                val selected = it == selectedRule
                val value = ruleService.getValue(it)

                val displayName = Constants.names[it]!!
                val valueDisplay = AllowedValues.getAllowedValues(it)[value]!!
                val display = "$displayName " +
                        (if (selected) " < " else "") +
                        valueDisplay +
                        (if (selected) " > " else "")

                if (selected) display.rgb(255, 255, 85)
                else display
            }
        }

    override fun handleInput(input: Key): InputReaction? = if (currentSubmenu == null) {
        when (input) {
            is ArrowKey -> {
                when (input.direction) {
                    ArrowKey.Direction.UP -> {
                        selectedSubmenu = selectedSubmenu
                            ?.let { it.ordinal - 1 }
                            ?.let { Submenu.entries.getOrNull(it) }
                            ?: Submenu.entries.last()
                        Redraw
                    }

                    ArrowKey.Direction.DOWN -> {
                        selectedSubmenu = selectedSubmenu
                            ?.let { it.ordinal + 1 }
                            ?.let { Submenu.entries.getOrNull(it) }
                            ?: Submenu.entries.first()
                        Redraw
                    }

                    else -> null
                }
            }

            is EnterKey -> {
                currentSubmenu = selectedSubmenu
                selectedRule = currentSubmenu!!.rules.first()
                Redraw
            }

            else -> null
        }
    } else {
        when (input) {
            is ArrowKey -> {
                when(input.direction) {
                    ArrowKey.Direction.UP -> {
                        selectedRule = selectedRule
                            ?.let { currentSubmenu!!.rules.indexOf(it) - 1 }
                            ?.let { currentSubmenu!!.rules.getOrNull(it) }
                            ?: currentSubmenu!!.rules.last()
                        Redraw
                    }

                    ArrowKey.Direction.DOWN -> {
                        selectedRule = selectedRule
                            ?.let { currentSubmenu!!.rules.indexOf(it) + 1 }
                            ?.let { currentSubmenu!!.rules.getOrNull(it) }
                            ?: currentSubmenu!!.rules.first()
                        Redraw
                    }

                    ArrowKey.Direction.LEFT -> {
                        val value = ruleService.getValue(selectedRule!!)
                        val allowedValues = AllowedValues.getAllowedValues(selectedRule!!)
                        val index = allowedValues.keys.indexOf(value)
                        val newValue = allowedValues.keys.toList().getOrNull(index - 1) ?: allowedValues.keys.last()

                        ruleService.setAnyValue(selectedRule!!, newValue)
                        Redraw
                    }

                    ArrowKey.Direction.RIGHT -> {
                        val value = ruleService.getValue(selectedRule!!)
                        val allowedValues = AllowedValues.getAllowedValues(selectedRule!!)
                        val index = allowedValues.keys.indexOf(value)
                        val newValue = allowedValues.keys.toList().getOrNull(index + 1) ?: allowedValues.keys.first()

                        ruleService.setAnyValue(selectedRule!!, newValue)
                        Redraw
                    }
                }
            }

            is EscapeKey -> {
                currentSubmenu = null
                Redraw
            }

            else -> null
        }
    }

    enum class Submenu(val title: String, vararg val rules: Rule<*>) {

        GENERAL("General", Rule.General.decks, Rule.General.shufflePercentage, Rule.General.blackjackPayout),
        DEALER("Dealer", Rule.Dealer.hitOnSoft17, Rule.Dealer.peeks, Rule.Dealer.checksBlackjack),
        TRIPPLE_SEVEN("Tripple seven", Rule.TrippleSeven.enabled, Rule.TrippleSeven.payout),
        SPLIT("Split", Rule.Split.amount, Rule.Split.acesAmount, Rule.Split.newCardsAfterAce),
        DOUBLE_DOWN("Double down", Rule.DoubleDown.afterSplit, Rule.DoubleDown.onlyOnSoft, Rule.DoubleDown.rescue),
        SURRENDER(
            "Surrender",
            Rule.Surrender.late,
            Rule.Surrender.early,
            Rule.Surrender.againstAce,
            Rule.Surrender.afterSplit
        ),
        FIVE_CARDS("Five-Card-Charlie", Rule.FiveCards.enabled, Rule.FiveCards.payout),
        INSURANCE("Insurance", Rule.Insurance.enabled, Rule.Insurance.payout),
        BUST_SIDE_BET("Bust side bet", Rule.BustSideBet.enabled, Rule.BustSideBet.payout),
        LUCKY_LUCKY_SIDE_BET(
            "Lucky-Lucky side bet",
            Rule.LuckyLuckySideBet.enabled,
            Rule.LuckyLuckySideBet.payout19,
            Rule.LuckyLuckySideBet.payout20,
            Rule.LuckyLuckySideBet.payout21,
            Rule.LuckyLuckySideBet.payout21Match,
            Rule.LuckyLuckySideBet.payoutSequence,
            Rule.LuckyLuckySideBet.payoutTripple,
            Rule.LuckyLuckySideBet.payoutSequenceMatch,
            Rule.LuckyLuckySideBet.payoutTrippleMatch
        ),
        ;

    }

}