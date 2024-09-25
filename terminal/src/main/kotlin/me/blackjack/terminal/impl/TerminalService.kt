package me.blackjack.terminal.impl

import me.blackjack.terminal.Key
import org.koin.core.annotation.Single
import me.blackjack.terminal.TerminalService as ITerminalService

@Single
internal class TerminalService(
    private val jLineService: JLineService,
    private val printService: PrintService,
    private val inputService: InputService,
) : ITerminalService {

    override fun onKeyPress(callback: (Key) -> Unit) {
        inputService.subscribe(callback)
    }

    override fun draw(lines: List<String>) {
        printService.draw(lines)
    }

    override fun shutdown() {
        printService.printBye()
        jLineService.close()
    }

}