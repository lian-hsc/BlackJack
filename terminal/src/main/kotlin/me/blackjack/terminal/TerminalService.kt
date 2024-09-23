package me.blackjack.terminal

interface TerminalService {

    val width: Int
    val height: Int

    fun clearScreen()

}