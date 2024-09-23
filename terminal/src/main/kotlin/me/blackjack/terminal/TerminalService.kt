package me.blackjack.terminal

import org.fusesource.jansi.AnsiConsole
import org.koin.core.annotation.Single
import me.blackjack.terminal.api.TerminalService as ITerminalService

@Single
internal class TerminalService : ITerminalService {

    init {
        if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
            AnsiConsole.systemInstall()
        }
    }

    override val width
        get() = System.getenv("COLUMNS")?.toIntOrNull() ?: 80

    override val height
        get() = System.getenv("LINES")?.toIntOrNull() ?: 24

    override fun clearScreen() {
        println("${Ansi.ESCAPE_SEQUENCE}${Ansi.Escape.ERASE_SCREEN}")
    }

}