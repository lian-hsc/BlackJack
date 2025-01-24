package me.blackjack.game.impl.menu

import me.blackjack.bank.format
import me.blackjack.game.impl.GameCollection
import me.blackjack.game.impl.game.FinishGame
import me.blackjack.game.impl.game.Game
import me.blackjack.game.impl.game.sidebet.BustSideBet
import me.blackjack.game.impl.game.sidebet.InsuranceSideBet
import me.blackjack.game.impl.game.sidebet.LuckyLuckSideBet
import me.blackjack.game.impl.game.sidebet.SideBet
import me.blackjack.menu.InputReaction
import me.blackjack.menu.Menu
import me.blackjack.menu.Pop
import me.blackjack.terminal.EnterKey
import me.blackjack.terminal.Key
import me.blackjack.terminal.rgb
import me.blackjack.terminal.stripStyle
import org.koin.core.annotation.Single
import kotlin.math.absoluteValue

@Single
internal class PayoutScreen(private val game: GameCollection) : Menu {

    override fun onPush() {
        assert(game.state == Game.State.FINISHED)
    }

    override fun getState(): List<String> = buildList {
        val (maxLength, state) = asState(game.payouts.map {
            Pair(
                when (it) {
                    is Game.HandPayout -> "Hand ${it.hand}: ${game.playerHands[it.hand]}"
                    is Game.SidebetPayout -> display(game.sideBets[it.bet])
                },
                it.amount
            )
        })

        addAll(state)

        add("-".repeat(maxLength + 5))
        val total = game.payouts.sumOf { it.amount }
        val totalFormated = total.absoluteValue.format().stripStyle()

        add(
            " ".repeat(maxLength + 2 - totalFormated.length) +
                    totalFormated.rgb(if (total > 0) "55FF55" else if (total == 0L) "AAAAAA" else "FF5555") +
                    " " + if (total > 0) "+" else if (total == 0L) " " else "-"
        )
    }

    override fun handleInput(input: Key): InputReaction? =
        if (input == EnterKey) {
            game.input(FinishGame)
            Pop()
        } else null

    private fun display(sideBet: SideBet) = buildString {
        append(
            when (sideBet) {
                is LuckyLuckSideBet -> "Lucky Lucky side bet"
                is BustSideBet -> "Bust side bet"
                is InsuranceSideBet -> "Insurance"
            }
        )

        append(" for ")
        append(sideBet.bet.format())
    }

    private fun asState(bets: List<Pair<String, Long>>): Pair<Int, List<String>> {
        val maxLength =
            bets.maxOf { it.first.stripStyle().length + it.second.absoluteValue.format().stripStyle().length } + 3

        return maxLength to bets.map { (name, amount) ->
            val formattedAmount = amount.absoluteValue.format().stripStyle()

            val space = maxLength - (name.stripStyle().length + formattedAmount.length)

            name + " ".repeat(space + 2) +
                    formattedAmount.rgb(if (amount > 0) "55FF55" else if (amount == 0L) "AAAAAA" else "FF5555") +
                    " " + if (amount > 0) "+" else if (amount == 0L) " " else "-"
        }
    }

}