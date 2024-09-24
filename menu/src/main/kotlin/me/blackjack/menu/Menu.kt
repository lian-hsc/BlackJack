package me.blackjack.menu

import me.blackjack.terminal.Key

interface Menu {

    fun onPush() = Unit

    /**
     * @return null if the menu should be popped, an error message otherwise
     */
    fun onPop(): String? = null

    /**
     * The returned lines are rendered one by one from top to bottom.
     * If there are more lines than the terminal height, the lines an exception is thrown.
     */
    fun getState(): List<String>

    fun getInputs(): Map<String, String>

    fun handleInput(input: Key): InputReaction?

}