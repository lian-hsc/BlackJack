package me.blackjack.bank.impl.menu

import me.blackjack.bank.impl.BankService
import me.blackjack.bank.format
import me.blackjack.menu.*
import me.blackjack.terminal.*
import org.koin.core.annotation.Single
import me.blackjack.bank.BankMenu as IBankMenu

@Single
internal class BankMenu(private val bankService: BankService) : IBankMenu {

    private var selected: Selected = Selected.ACTION
    private var action: Action = Action.GET_LOAN
    private var amount: Long = 1

    private val amounts = listOf<Long>(1, 5, 10, 50, 100, 500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000)

    private val availableAmounts
        get() = amounts
            .filter { action == Action.GET_LOAN || (it <= bankService.loan && it <= bankService.availableCapital) }

    override fun getState(): List<String> = listOf(
        "Current capital: ${bankService.capital.format()}",
        "Current loan: ${bankService.loan.format()}",
        "",
        action.selectorIf(selected == Selected.ACTION),
        if (action == Action.REPAY_LOAN && bankService.loan == 0L) "(No loan to repay)".highlightIf(selected == Selected.AMOUNT)
        else amount.format().selectorIf(selected == Selected.AMOUNT),
        "Finish".highlightIf(selected == Selected.FINISH),
    )

    override fun handleInput(input: Key): InputReaction? = when(input) {
        is ArrowKey -> {
            when(input.direction) {
                ArrowKey.Direction.UP -> selected = Selected.entries[(selected.ordinal - 1).mod(Selected.entries.size)]
                ArrowKey.Direction.DOWN -> selected = Selected.entries[(selected.ordinal + 1).mod(Selected.entries.size)]

                ArrowKey.Direction.LEFT -> when(selected) {
                    Selected.ACTION -> {
                        action = Action.entries[(action.ordinal - 1).mod(Action.entries.size)]
                        amount = availableAmounts.firstOrNull() ?: 1
                    }
                    Selected.AMOUNT -> amount = availableAmounts[(availableAmounts.indexOf(amount) - 1).mod(availableAmounts.size)]
                    else -> {}
                }

                ArrowKey.Direction.RIGHT -> when(selected) {
                    Selected.ACTION -> {
                        action = Action.entries[(action.ordinal + 1).mod(Action.entries.size)]
                        amount = availableAmounts.firstOrNull() ?: 1
                    }
                    Selected.AMOUNT -> amount = availableAmounts[(availableAmounts.indexOf(amount) + 1).mod(availableAmounts.size)]
                    else -> {}
                }
            }

            Redraw
        }

        is EnterKey -> {
            if (selected == Selected.FINISH) {
                when(action) {
                    Action.GET_LOAN -> bankService.getLoan(amount)
                    Action.REPAY_LOAN -> {
                        if (amount <= bankService.loan && amount <= bankService.availableCapital) {
                            bankService.repayLoan(amount)

                            if (amount !in availableAmounts) {
                                amount = availableAmounts.lastOrNull() ?: 0
                            }
                        }
                    }
                }

                Redraw
            } else null
        }

        is EscapeKey -> Pop()

        else -> null
    }

    enum class Selected {
        ACTION,
        AMOUNT,
        FINISH,
    }

    enum class Action(private val text: String) {
        GET_LOAN("Get loan"),
        REPAY_LOAN("Repay loan"),
        ;

        override fun toString(): String = text
    }

}