package me.blackjack.game.impl.menu

import me.blackjack.bank.format
import me.blackjack.game.impl.GameCollection
import me.blackjack.game.impl.game.*
import me.blackjack.game.impl.game.Double
import me.blackjack.menu.*
import me.blackjack.terminal.*
import org.koin.core.annotation.Single
import kotlin.reflect.KClass

@Single
internal class GameMenu(private val game: GameCollection, private val payoutScreen: PayoutScreen) : Menu {

    private var selector = 0
    private var insurance = 0L

    override fun onPush() {
        assert(game.state == Game.State.PLAYER)
        selector = 2
    }

    override fun getState(): List<String> = buildList {
        add(
            "Dealer's hand: " +
                    if (game.dealerSecondCardHidden) getDealerFirstHand()
                    else game.dealerHand.toString()
        )

        game.playerHands.forEachIndexed { index, hand ->
            add("Hand ${(index + 1)}: $hand".let {
                if (game.state == Game.State.PLAYER && game.currentPlayerHand == index) it.underline() else it
            })
        }

        val actions = game.getPossibleActions()

        add(if (actions[selector] == Insure::class) insurance.format() else "")
        if (actions.any { it == Surrender::class }) {
            if (selector == 0) add("Proceed".selector())
            else {
                val hands = game.playerHands.withIndex().filter { game.canSurrender(it.value) }
                add("Surrender Hand ${hands[selector - 1].index + 1}".selector())
            }
        } else {
            add(actions[selector].asAction()
                .selectorIf(actions.size > 1)
                .highlightIf(actions.size == 1)
            )
        }
    }

    override fun handleInput(input: Key): InputReaction? = when (input) {
        is ArrowKey -> {
            when (input.direction) {
                ArrowKey.Direction.LEFT -> {
                    selector = (selector - 1).mod(game.getPossibleActions().size)
                }

                ArrowKey.Direction.RIGHT -> {
                    selector = (selector + 1).mod(game.getPossibleActions().size)
                }

                else -> {}
            }

            Redraw
        }

        is AlphaNumericKey -> {
            if (game.getPossibleActions()[selector] == Insure::class) {
                if (input.char.isDigit()) {
                    insurance = insurance * 10 + input.char.toString().toLong()
                }

                Redraw
            } else null
        }

        is BackspaceKey -> {
            if (game.getPossibleActions()[selector] == Insure::class) {
                insurance /= 10
                Redraw
            } else null
        }

        is EnterKey -> {
            val actions = game.getPossibleActions()
            if (actions.any { it == Surrender::class }) {
                if (selector == 0) game.input(Proceed)
                else {
                    val hands = game.playerHands.withIndex().filter { game.canSurrender(it.value) }
                    game.input(Surrender(hands[selector - 1].index))
                }

                selector = 0
                Redraw
            } else if (actions[selector] == Insure::class) {
                game.input(Insure(insurance))

                selector = 0
                Redraw
            }
            else if (actions[selector] == Payout::class) {
                game.input(Payout)
                Push(payoutScreen, MenuService.PushType.REPLACE)
            } else {
                game.input(actions[selector].objectInstance as PlayerInput)

                selector = 0
                Redraw
            }
        }

        is EscapeKey -> {
            game.input(AbortGame)
            Pop(2)
        }

        else -> null
    }

    private fun getDealerFirstHand() =
        game.dealerHand[0].toString() + (" ??".rgb("AAAAAA") + " (${game.dealerHand[0].rank.value} + ?)")

    private fun KClass<out PlayerInput>.asAction() = when (this) {
        Insure::class -> "Insure"
        Hit::class -> "Hit"
        Stand::class -> "Stand"
        Double::class -> "Double"
        Split::class -> "Split"
        SurrenderCurrent::class -> "Surrender"
        Proceed::class -> "Proceed"
        Payout::class -> "Payout"
        else -> "Unknown Action ($simpleName)"
    }

}