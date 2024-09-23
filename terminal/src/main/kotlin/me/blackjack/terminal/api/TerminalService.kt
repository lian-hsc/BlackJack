package me.blackjack.terminal.api

interface TerminalService {

    val width: Int
    val height: Int

    fun clearScreen()

}