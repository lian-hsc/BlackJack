package me.blackjack.terminal.impl

import me.blackjack.terminal.*
import org.fusesource.jansi.AnsiConsole
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.koin.core.annotation.Single
import java.io.Reader
import kotlin.concurrent.thread
import me.blackjack.terminal.TerminalService as ITerminalService

@Single
internal class TerminalService : ITerminalService {

    private val subscribers = mutableListOf<(Key) -> Unit>()

    private val terminal: Terminal
    private val reader: Reader
    private val buffer = mutableListOf<Int>()

    private val ansiEscapeRegex = Regex("\u001B\\[[;\\d]*m")

    init {
        if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
            AnsiConsole.systemInstall()
        }

        terminal = TerminalBuilder.builder()
            .jna(true)
            .system(true)
            .build()

        terminal.enterRawMode()
        reader = terminal.reader()

        thread {
            val buffered = reader.buffered()
            var input = 0

            while (runCatching { input = buffered.read() }.isSuccess) {
                buffer.add(input)

                if (input == KeyCodes.ESC) {
                    Thread.sleep(1)

                    if (buffered.ready()) buffer.add(buffered.read())
                }

                handleInput()
            }
        }
    }

    override fun onKeyPress(callback: (Key) -> Unit) {
        subscribers.add(callback)
    }

    override fun draw(lines: List<String>) {
        if (lines.size > terminal.height) {
            throw IllegalStateException("Menu is too large to be displayed on the terminal")
        }

        val top = (terminal.height - lines.size) / 2
        val bottom = terminal.height - lines.size - top

        repeat(top) { println(finalize("")) }
        lines.forEach { println(finalize(it)) }
        repeat(bottom) { println(finalize("")) }
    }

    override fun shutdown() {
        reader.close()
        terminal.close()
    }

    private fun finalize(text: String): String {
        val actualText = text.bgRgb(51, 51, 51)
        val actualLength = ansiEscapeRegex.replace(actualText, "").length

        val leftPadding = (terminal.width - actualLength) / 2 + if (actualLength % 2 == 0) 0 else 1
        val rightPadding = terminal.width - actualLength - leftPadding

        return " ".repeat(leftPadding) + actualText + " ".repeat(rightPadding)
    }

    private fun handleInput() {
        val key =
            if (buffer.size > 1) {
                when (buffer) {
                    KeyCodes.ARROW_UP_SEQUENCE -> ArrowKey(ArrowKey.Direction.UP)
                    KeyCodes.ARROW_DOWN_SEQUENCE -> ArrowKey(ArrowKey.Direction.DOWN)
                    KeyCodes.ARROW_RIGHT_SEQUENCE -> ArrowKey(ArrowKey.Direction.RIGHT)
                    KeyCodes.ARROW_LEFT_SEQUENCE -> ArrowKey(ArrowKey.Direction.LEFT)
                    else -> null
                }
            } else if (buffer.first() == KeyCodes.ESC) EscapeKey
            else if (buffer.first() == KeyCodes.CARRIAGE_RETURN) EnterKey
            else if (buffer.first() == KeyCodes.BACKSPACE) BackspaceKey
            else AlphaNumericKey(buffer.first().toChar())

        if (key == null) return

        buffer.clear()
        subscribers.forEach { it(key) }
    }

}