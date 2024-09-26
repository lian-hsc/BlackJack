package me.blackjack.game.impl

import me.blackjack.menu.InputReaction
import me.blackjack.terminal.Key
import org.koin.core.annotation.Factory
import me.blackjack.game.GameMenu as IGameMenu

@Factory
internal class GameMenu(private val game: GameCollection) : IGameMenu {

    override fun getState(): List<String> {
        TODO("Not yet implemented")
    }

    override fun handleInput(input: Key): InputReaction? {
        TODO("Not yet implemented")
    }

}