package me.blackjack.game.impl

import me.blackjack.bank.BankService
import me.blackjack.game.impl.game.*
import me.blackjack.game.impl.game.Game
import me.blackjack.game.impl.model.Deck
import me.blackjack.game.impl.model.Hand
import me.blackjack.rule.Rule
import me.blackjack.rule.RuleService
import org.koin.core.annotation.Single
import kotlin.math.min
import kotlin.reflect.KClass
import me.blackjack.game.GameCollection as IGameCollection


@Single
internal class GameCollection(
    private val bankService: BankService,
    private val ruleService: RuleService,
) : IGameCollection {

    private var deck =
        Deck(ruleService.getValue(Rule.General.decks), ruleService.getValue(Rule.General.shufflePercentage))

    private var currentGame: Game? = null

    private var previousBet: Long = min(500, bankService.availableCapital)

    val state get() = currentGame?.state ?: Game.State.UNINITIALIZED
    val bet get() = currentGame?.bet ?: error("No game in progress")
    val sideBets get() = currentGame?.sideBets ?: error("No game in progress")
    val playerHands get() = currentGame?.playerHands ?: error("No game in progress")
    val currentPlayerHand get() = currentGame?.currentHandIndex ?: error("No game in progress")
    val dealerHand get() = currentGame?.dealerHand ?: error("No game in progress")
    val dealerSecondCardHidden get() = currentGame?.dealerSecondCardHidden ?: error("No game in progress")
    val payouts get() = currentGame?.payouts ?: error("No game in progress")

    init {
        deck.shuffle()
    }

    override fun reset() {
        currentGame = null
        previousBet = min(500, bankService.availableCapital)
        deck = Deck(ruleService.getValue(Rule.General.decks), ruleService.getValue(Rule.General.shufflePercentage))
        deck.shuffle()
    }

    fun input(input: PlayerInput) {
        if (currentGame == null) {
            if (input != Initiate) throw IllegalStateException("First input must be Initiate")
            currentGame = createGame()
            return
        }

        if (input is SetBet) previousBet = input.bet
        if (currentGame!!.input(input)) {
            currentGame = if (input == AbortGame) null else createGame()
        }
    }

    fun getPossibleActions(): List<KClass<out PlayerInput>> =
        currentGame?.getPossibleActions() ?: listOf(Initiate::class)

    fun canSurrender(hand: Hand) = currentGame!!.canHandSurrender(hand)

    private fun createGame(): Game {
        if (deck.shouldShuffle) deck.shuffle()
        return Game(deck, bankService, ruleService, previousBet)
    }

}