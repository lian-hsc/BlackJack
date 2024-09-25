package me.blackjack.terminal.impl

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import org.koin.core.annotation.Single

@Single
internal class JLineService {

    val terminal: Terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .build()

    init {
        terminal.enterRawMode()
    }

    val reader: NonBlockingReader = terminal.reader()

    fun close() {
        reader.close()
        terminal.close()
    }

}