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

@Single
internal class MenuService(private val terminalService: TerminalService) : IMenuService {

    private val _stack: MutableList<Menu> = mutableListOf()

    override val stack: List<Menu> = Collections.unmodifiableList(_stack)

    private val currentMenu
        get() = _stack.lastOrNull()

    init {
        terminalService.onKeyPress {
            val reaction = currentMenu?.handleInput(it) ?: return@onKeyPress

            when (reaction) {
                is Message -> redraw(reaction.message)
                is Pop -> pop(reaction.times)
                is Push -> push(reaction.menu, reaction.pushType)
                Redraw -> redraw(null)
            }
        }
    }

    override fun redraw() {
        redraw(null)
    }

    override fun push(menu: Menu, pushType: MenuService.PushType) = synchronized(_stack) {
        if (pushType == MenuService.PushType.REPLACE && currentMenu != null) {
            val error = currentMenu!!.onPop()
            if (error == null) _stack.removeLast()
            else {
                redraw(error)
                return
            }
        }

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

        val lines = mutableListOf<String>()

        lines.addAll(state)

        if (message != null) {
            lines.add("")
            lines.add(message)
        }

        terminalService.draw(lines)
    }

}