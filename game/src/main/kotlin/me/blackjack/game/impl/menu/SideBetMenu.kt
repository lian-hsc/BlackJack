package me.blackjack.game.impl.menu

import me.blackjack.bank.format
import me.blackjack.game.impl.GameCollection
import me.blackjack.game.impl.game.PregameSideBet
import me.blackjack.menu.*
import me.blackjack.terminal.*
import org.koin.core.annotation.Single

@Single
internal class SideBetMenu(val game: GameCollection) : Menu {

    private var index: Int = 0
    private var selector: Int = 0

    private var sideBet: Long = 1

    override fun onPush() {
        index = 2
    }

    override fun getState(): List<String> = listOf(
        "Add side bet",
        (if (selector == 0) "Bust" else "Lucky Lucky").selectorIf(index == 0),
        sideBet.format().highlightIf(index == 1).rgb(255, 170, 0),
        "Confirm".highlightIf(index == 2),
    )

    override fun handleInput(input: Key): InputReaction? = when (input) {
        is ArrowKey -> {
            when (input.direction) {
                ArrowKey.Direction.UP -> {
                    index = (index - 1).mod(3)
                }

                ArrowKey.Direction.DOWN -> {
                    index = (index + 1).mod(3)
                }

                ArrowKey.Direction.LEFT -> {
                    if (index == 0) {
                        selector = (selector - 1).mod(2)
                    }
                }

                ArrowKey.Direction.RIGHT -> {
                    if (index == 0) {
                        selector = (selector + 1).mod(2)
                    }
                }
            }

            Redraw
        }

        is AlphaNumericKey -> {
            if (index == 1) {
                if (input.char.isDigit()) {
                    val newValue = sideBet * 10 + input.char.toString().toLong()
                    sideBet = newValue
                }

                Redraw
            } else null
        }

        is BackspaceKey -> {
            if (index == 1) {
                val newValue = sideBet / 10
                sideBet = newValue

                Redraw
            } else null
        }

        is EnterKey -> {
            if (index == 2) {
                try {
                    game.input(
                        PregameSideBet(
                            if (selector == 0) PregameSideBet.Type.BUST else PregameSideBet.Type.LUCKY_LUCKY,
                            sideBet
                        )
                    )
                    Pop()
                } catch (_: IllegalStateException) {
                    Message("You don't have enough money to bet ${game.bet.format()}".bg("FF5555"))
                }
            } else null
        }

        is EscapeKey -> Pop()
        else -> null
    }
}