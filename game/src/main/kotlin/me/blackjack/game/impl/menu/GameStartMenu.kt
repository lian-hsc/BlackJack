package me.blackjack.game.impl.menu

import me.blackjack.game.impl.GameCollection
import me.blackjack.game.impl.game.Game
import me.blackjack.game.impl.game.Initiate
import me.blackjack.menu.*
import me.blackjack.terminal.EnterKey
import me.blackjack.terminal.EscapeKey
import me.blackjack.terminal.Key
import org.koin.core.annotation.Single
import me.blackjack.game.GameMenu as IGameMenu

@Single
internal class GameStartMenu(
    private val menuService: MenuService,
    private val game: GameCollection,
    private val betMenu: BetMenu,
) : IGameMenu {

    override fun onPush() {
        if (game.state != Game.State.UNINITIALIZED) {
            menuService.push(betMenu, MenuService.PushType.REPLACE)
        }
    }

    override fun getState(): List<String> = listOf(
        "So let's play BlackJack!",
        "Start game".highlight()
    )

    override fun handleInput(input: Key): InputReaction? = when (input) {
        is EnterKey -> {
            game.input(Initiate)
            Push(betMenu, MenuService.PushType.REPLACE)
        }

        is EscapeKey -> Pop()
        else -> null
    }


}