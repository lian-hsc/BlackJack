package me.blackjack.game.impl

import me.blackjack.bank.BankService
import me.blackjack.game.impl.game.*
import me.blackjack.game.impl.game.Game
import me.blackjack.game.impl.model.Deck
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService
import org.koin.core.annotation.Factory
import kotlin.reflect.KClass


@Factory
internal class GameCollection(
    private val bankService: BankService,
    private val ruleService: RuleService,
) {

    private val deck = Deck(ruleService.getValue(Rule.General.decks), ruleService.getValue(Rule.General.shufflePercentage))

    private var currentGame: Game = createGame()
    private var previousBet: Long = listOf(500, bankService.availableCapital).min()

    init {
        deck.shuffle()
    }

    fun input(input: PlayerInput): GameReaction {
        if (input is SetBet) previousBet = input.bet

        val reaction = currentGame.input(input)

        if (reaction == GameDone) currentGame = createGame()

        return reaction
    }

    fun getPossibleActions(): List<KClass<out PlayerInput>> = currentGame.getPossibleActions()


    private fun createGame() = Game(deck, bankService, ruleService, previousBet)

}