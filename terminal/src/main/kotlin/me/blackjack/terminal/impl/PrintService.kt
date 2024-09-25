package me.blackjack.terminal.impl

import me.blackjack.terminal.bgRgb
import me.blackjack.terminal.impl.constant.Ansi
import org.koin.core.annotation.Single

@Single
internal class PrintService(private val jline: JLineService) {

    private var cachedHeight: Int = jline.terminal.height
    private var cachedWidth: Int = jline.terminal.width
    private var cachedScreen = emptyMap<Int, String>()

    private val ansiEscapeRegex = Regex("\u001B\\[[;\\d]*m")

    init {
        print(Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.INVISIBLE)
        redrawWholeScreen()
    }

    fun draw(lines: List<String>) {
        if (lines.size > jline.terminal.height) {
            throw IllegalStateException("Menu is too large to be displayed on the terminal")
        }

        if (jline.terminal.height != cachedHeight || jline.terminal.width != cachedWidth) {
            cachedScreen = buildScreen(lines)
            cachedHeight = jline.terminal.height
            cachedWidth = jline.terminal.width
            redrawWholeScreen()
            return
        }

        val difference = calculateDifference(lines)
        redrawDifference(difference)
    }

    fun printBye() {
        print(Ansi.ESCAPE_SEQUENCE + Ansi.Erase.SCREEN)
        print(Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.toLine(1))

        println(finalize(""))
        println(finalize("Thank you for playing!"))
        println(finalize(""))

        print(Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.VISIBLE)
    }

    private fun calculateDifference(lines: List<String>): Map<Int, String?> {
        val newScreen = buildScreen(lines)
        val difference = mutableMapOf<Int, String?>()

        val redrawLines = (newScreen.keys - cachedScreen.keys) +
                newScreen.keys.intersect(cachedScreen.keys).filter { newScreen[it] != cachedScreen[it] }
        val clearLines = cachedScreen.keys - newScreen.keys

        difference.putAll(redrawLines.map { it to newScreen[it] })
        difference.putAll(clearLines.map { it to null })

        cachedScreen = newScreen

        return difference
    }

    private fun redrawDifference(difference: Map<Int, String?>) {
        difference.forEach { (index, text) ->
            print(
                Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.toLine(index + 1) +
                        Ansi.ESCAPE_SEQUENCE + Ansi.Erase.LINE +
                        finalize(text ?: "")
            )
        }
    }

    private fun redrawWholeScreen() {
        repeat(jline.terminal.height) {
            print(
                Ansi.ESCAPE_SEQUENCE + Ansi.Cursor.toLine(it + 1) +
                        Ansi.ESCAPE_SEQUENCE + Ansi.Erase.LINE +
                        finalize(cachedScreen[it] ?: "")
            )
        }
    }

    private fun buildScreen(lines: List<String>): Map<Int, String> {
        val top = (jline.terminal.height - lines.size) / 2
        return lines
            .mapIndexed { index, s -> top + index to s }
            .filter { (_, text) -> text.isNotBlank() }
            .toMap()
    }

    private fun finalize(text: String): String {
        val actualLength = ansiEscapeRegex.replace(text, "").length

        val pads = jline.terminal.width - actualLength

        val rightPads = pads / 2
        val leftPads = pads - rightPads

        return (" ".repeat(leftPads) + text + " ".repeat(rightPads)).bgRgb(85, 85, 85)
    }

}