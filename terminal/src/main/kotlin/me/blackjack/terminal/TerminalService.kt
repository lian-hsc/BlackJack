package me.blackjack.terminal

interface TerminalService {

    fun onKeyPress(callback: (Key) -> Unit)

    fun draw(lines: List<String>)

    fun shutdown()

}