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

    private var currentScreen = mapOf<Int, String>()

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

        print(Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.INVISIBLE)
        repeat(terminal.height) {
            print(
                Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.toLine(it + 1) +
                        Ansi.ESCAPE_SEQUENCE + Ansi.Erase.LINE +
                        finalize("")
            )
        }
    }

    override fun onKeyPress(callback: (Key) -> Unit) {
        subscribers.add(callback)
    }

    override fun draw(lines: List<String>) {
        if (lines.size > terminal.height) {
            throw IllegalStateException("Menu is too large to be displayed on the terminal")
        }

        val difference = calculateDifference(lines)
        redraw(difference)
    }

    override fun shutdown() {
        print(Ansi.ESCAPE_SEQUENCE + Ansi.Erase.SCREEN)
        print(Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.toLine(1))

        println(finalize(""))
        println(finalize("Thank you for playing!"))
        println(finalize(""))

        print(Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.VISIBLE)

        reader.close()
        terminal.close()
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

    private fun calculateDifference(lines: List<String>): Map<Int, String?> {
        val top = (terminal.height - lines.size) / 2

        val newScreen = lines
            .mapIndexed { index, s -> top + index to s }
            .filter { (_, text) -> text.isNotBlank() }
            .toMap()

        val difference = mutableMapOf<Int, String?>()

        val redrawLines = (newScreen.keys - currentScreen.keys) +
                newScreen.keys.intersect(currentScreen.keys).filter { newScreen[it] != currentScreen[it] }
        val clearLines = currentScreen.keys - newScreen.keys

        difference.putAll(redrawLines.map { it to newScreen[it] })
        difference.putAll(clearLines.map { it to null })

        currentScreen = newScreen

        return difference
    }

    private fun redraw(difference: Map<Int, String?>) {
        difference.forEach { (index, text) ->
            print(
                Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.toLine(index + 1) +
                        Ansi.ESCAPE_SEQUENCE + Ansi.Erase.LINE +
                        finalize(text ?: "")
            )
        }
    }

    private fun finalize(text: String): String {
        val actualLength = ansiEscapeRegex.replace(text, "").length

        val leftPadding = (terminal.width - actualLength) / 2
        val rightPadding = terminal.width - actualLength - leftPadding

        return (" ".repeat(leftPadding) + text + " ".repeat(rightPadding)).bgRgb(85, 85, 85)
    }

}