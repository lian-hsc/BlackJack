package me.blackjack.game.impl.menu

import me.blackjack.bank.format
import me.blackjack.game.impl.GameCollection
import me.blackjack.game.impl.game.SetBet
import me.blackjack.game.impl.game.Start
import me.blackjack.menu.*
import me.blackjack.terminal.*
import org.koin.core.annotation.Single

@Single
internal class BetMenu(
    private val game: GameCollection,
    private val sideBetMenu: SideBetMenu,
    private val gameMenu: GameMenu,
) : Menu {

    private var index: Int = 0

    override fun onPush() {
        index = 2
    }

    override fun getState(): List<String> = listOf(
        "You current bet is ${game.bet.format().highlightIf(index == 0).rgb(255, 170, 0)}",
        "Add side bet".highlightIf(index == 1),
        "Start game".highlightIf(index == 2),
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

                else -> {}
            }

            Redraw
        }

        is AlphaNumericKey -> {
            if (index == 0) {
                val currentValue = game.bet
                if (input.char.isDigit()) {
                    val newValue = currentValue * 10 + input.char.toString().toLong()

                    game.input(SetBet(newValue))
                }

                Redraw
            } else null
        }


        is BackspaceKey -> {
            if (index == 0) {
                val currentValue = game.bet
                val newValue = currentValue / 10

                game.input(SetBet(newValue))
                Redraw
            } else null
        }

        is EnterKey -> {
            when (index) {
                1 -> {
                    Push(sideBetMenu)
                }
                2 -> {
                    try {
                        game.input(Start)
                        Push(gameMenu)
                    } catch (_: IllegalStateException) {
                        Message("You don't have enough money to bet ${game.bet.format()}".bg("FF5555"))
                    }
                }
                else -> null
            }
        }

        is EscapeKey -> Pop()
        else -> null
    }
}