package me.blackjack.rule.impl.menu

import me.blackjack.menu.*
import me.blackjack.rule.Rule
import me.blackjack.rule.impl.RuleService
import me.blackjack.rule.impl.stored.StoredRuleService
import me.blackjack.terminal.*
import org.koin.core.annotation.Single
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import me.blackjack.rule.menu.RuleMenu as IRuleMenu

@Single
internal class RuleMenu(private val ruleService: RuleService) : IRuleMenu {

    private var currentSubmenu: Submenu? = null

    private var selectedSubmenu: Submenu? = Submenu.GENERAL

    private var selectedRule: Rule<*>? = null

    override fun onPush() {
        currentSubmenu = null
        selectedSubmenu = Submenu.GENERAL
    }

    override fun getState(): List<String> =
        if (currentSubmenu == null) {
            Submenu.entries.map { it.title.highlightIf(it == selectedSubmenu) }
        } else {
            currentSubmenu!!.rules.map {
                val selected = it == selectedRule
                val value = ruleService.getValue(it)

                val displayName = Constants.names[it]!!
                val valueDisplay = AllowedValues.getAllowedValues(it)[value]!!

                "$displayName ${valueDisplay.selectorIf(selected)}".highlightIf(selected)
            }
        }

    override fun handleInput(input: Key): InputReaction? = if (currentSubmenu == null) {
        when (input) {
            is ArrowKey -> {
                when (input.direction) {
                    ArrowKey.Direction.UP, ArrowKey.Direction.DOWN -> {
                        selectedSubmenu = Submenu.entries[(
                                selectedSubmenu
                                    ?.ordinal
                                    ?.let { if (input.direction == ArrowKey.Direction.UP) it - 1 else it + 1 }
                                    ?: 0)
                            .mod(Submenu.entries.size)]
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

            is EscapeKey -> Pop()

            else -> null
        }
    } else {
        when (input) {
            is ArrowKey -> {
                when (input.direction) {
                    ArrowKey.Direction.UP, ArrowKey.Direction.DOWN -> {
                        selectedRule = currentSubmenu!!.rules[(
                                currentSubmenu!!
                                    .rules
                                    .indexOf(selectedRule) +
                                        if (input.direction == ArrowKey.Direction.UP) -1 else 1)
                            .mod(currentSubmenu!!.rules.size)]
                        Redraw
                    }

                    ArrowKey.Direction.LEFT, ArrowKey.Direction.RIGHT -> {
                        val allowedValues = AllowedValues.getAllowedValues(selectedRule!!)

                        ruleService.setAnyValue(
                            selectedRule!!,
                            allowedValues.keys.toList()[(
                                    allowedValues
                                        .keys
                                        .indexOf(ruleService.getValue(selectedRule!!)) +
                                            if (input.direction == ArrowKey.Direction.LEFT) -1 else 1)
                                .mod(allowedValues.keys.size)]
                        )
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