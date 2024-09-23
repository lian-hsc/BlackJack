package me.blackjack.menu.impl

import me.blackjack.menu.*
import me.blackjack.menu.MenuService
import me.blackjack.terminal.TerminalService
import me.blackjack.terminal.bgRgb
import me.blackjack.terminal.rgb
import org.koin.core.annotation.Single
import java.util.Collections
import kotlin.concurrent.thread
import me.blackjack.menu.MenuService as IMenuService

@Single(createdAtStart = true)
class MenuService(private val terminalService: TerminalService) : IMenuService {

    private val _stack: MutableList<Menu> = mutableListOf()

    override val stack: List<Menu> = Collections.unmodifiableList(_stack)

    private val currentMenu
        get() = _stack.lastOrNull()

    private val ansiEscapeRegex = Regex("\u001B\\[[;\\d]*m")

    init {
        thread {
            var input: String
            while (readlnOrNull().also { input = it ?: "" } != null) {
                val reaction = currentMenu?.handleInput(input) ?: continue

                when (reaction) {
                    is Message -> redraw(reaction.message)
                    is Pop -> pop(reaction.times)
                    is Push -> push(reaction.menu, reaction.pushType)
                    Redraw -> redraw(null)
                }
            }

            terminalService.clearScreen()
        }
    }

    override fun push(menu: Menu, pushType: MenuService.PushType) = synchronized(_stack) {
        if (pushType == MenuService.PushType.REPLACE) _stack.removeLast()

        _stack.add(menu)
        menu.onPush()

        redraw(null)
    }

    override fun pop(times: Int) = synchronized(_stack) {
        for (i in 0 until times) {
            if (currentMenu == null) return
            val error = currentMenu!!.onPop()

            if (error == null) {
                _stack.removeLast()
                redraw(null)
            } else {
                redraw(error)
            }
        }
    }

    private fun redraw(message: String?) {
        val state = currentMenu?.getState() ?: emptyList()
        val inputs = currentMenu?.getInputs() ?: emptyMap()

        val requiredLines = state.size +
                (if (inputs.isEmpty()) 0 else (inputs.size + 1)) +
                (if (message == null) 0 else 2) +
                1 // use for the prompt

        if (requiredLines > terminalService.height - 1) {
            throw IllegalStateException("Menu is too large to be displayed on the terminal")
        }

        val top = (terminalService.height - requiredLines) / 2
        val bottom = terminalService.height - requiredLines - top

        repeat(top) { println(finalize("")) }

        state.forEach { println(finalize(it)) }

        if (inputs.isNotEmpty()) {
            println(finalize(""))
            inputs.forEach { (key, value) -> println(finalize("${key.rgb(255, 170, 0)}: $value")) }
        }

        if (message != null) {
            println(finalize(""))
            println(finalize(message))
        }

        repeat(bottom) { println(finalize("")) }
    }

    private fun finalize(text: String): String {
        val actualText = text.bgRgb(51, 51, 51)
        val actualLength = ansiEscapeRegex.replace(actualText, "").length

        val leftPadding = (terminalService.width - actualLength) / 2 + if (actualLength % 2 == 0) 0 else 1
        val rightPadding = terminalService.width - actualLength - leftPadding

        return " ".repeat(leftPadding) + actualText + " ".repeat(rightPadding)
    }

}