package me.blackjack.application

import me.blackjack.bank.BankMenu
import me.blackjack.bank.BankService
import me.blackjack.bank.impl.BankModule
import me.blackjack.game.GameCollection
import me.blackjack.game.GameMenu
import me.blackjack.game.impl.GameModule
import me.blackjack.menu.*
import me.blackjack.menu.impl.MenuModule
import me.blackjack.rule.RuleService
import me.blackjack.terminal.impl.TerminalModule
import me.blackjack.rule.impl.RuleModule
import me.blackjack.rule.menu.RuleMenu
import me.blackjack.terminal.*
import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import kotlin.system.exitProcess

fun main() {
    startKoin {
        modules(
            TerminalModule().module,
            MenuModule().module,
            RuleModule().module,
            BankModule().module,
            GameModule().module,
            ApplicationModule().module,
        )
    }
}

@Single(createdAtStart = true)
class Application(
    menu: MenuService,
    private val ruleMenu: RuleMenu,
    private val bankMenu: BankMenu,
    private val gameCollection: GameCollection,
    private val gameMenu: GameMenu,
    private val ruleService: RuleService,
    private val bankService: BankService,
    private val terminalService: TerminalService,
) {

    init {
        menu.push(RootMenu())

        Runtime.getRuntime().addShutdownHook(Thread {
            ruleService.saveRules()
            bankService.save()

            terminalService.shutdown()
        })
    }

    inner class RootMenu : Menu {

        private var index = 0

        override fun getState(): List<String> = listOf(
            "Rules".highlightIf(index == 0),
            "Bank".highlightIf(index == 1),
            "Play".highlightIf(index == 2),
        )

        override fun handleInput(input: Key): InputReaction? = when(input) {
            is ArrowKey -> {
                when (input.direction) {
                    ArrowKey.Direction.UP, ArrowKey.Direction.DOWN -> {
                        index = (index + if (input.direction == ArrowKey.Direction.UP) -1 else 1).mod(3)
                        Redraw
                    }
                    else -> null
                }
            }

            is EnterKey -> {
                when (index) {
                    0 -> Push(ruleMenu)
                    1 -> Push(bankMenu)
                    2 -> {
                        gameCollection.reset()
                        Push(gameMenu)
                    }
                    else -> null
                }
            }

            is EscapeKey -> exitProcess(0)
            else -> null
        }

    }

}