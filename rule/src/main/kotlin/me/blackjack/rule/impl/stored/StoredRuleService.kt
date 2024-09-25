package me.blackjack.rule.impl.stored

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import me.blackjack.rule.Rule
import org.koin.core.annotation.Single
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.javaField

@Single
internal class StoredRuleService {

    private lateinit var lookupMap: Map<Rule<*>, StoredRule<*>>

    @Suppress("UNCHECKED_CAST")
    private fun <Type> Map<String, JsonElement>.by(
        reference: KProperty0<Rule<Type>>,
        default: Type,
    ): StoredRule<Type> =
        (reference.javaField!!.declaringClass.simpleName + reference.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase()
            else it.toString()
        }).let {
            StoredRule(
                it,
                reference.get(),
                serializer(reference.returnType.arguments.first().type!!) as KSerializer<Type>,
                this[it],
                default,
            )
        }

    fun initLookupMap(values: Map<String, JsonElement>) = with(values) {
        lookupMap = listOf(
            by(Rule.General::decks, 8),
            by(Rule.General::shufflePercentage, 0.8),
            by(Rule.General::blackjackPayout, 1.5),
            by(Rule.General::push22, false),
            by(Rule.Dealer::hitOnSoft17, false),
            by(Rule.Dealer::peeks, true),
            by(Rule.Dealer::checksBlackjack, false),
            by(Rule.TrippleSeven::enabled, false),
            by(Rule.TrippleSeven::payout, null),
            by(Rule.Split::amount, null),
            by(Rule.Split::acesAmount, null),
            by(Rule.Split::newCardsAfterAce, false),
            by(Rule.DoubleDown::afterSplit, false),
            by(Rule.DoubleDown::onlyOnSoft, false),
            by(Rule.DoubleDown::rescue, false),
            by(Rule.Surrender::late, true),
            by(Rule.Surrender::early, false),
            by(Rule.Surrender::againstAce, true),
            by(Rule.Surrender::afterSplit, false),
            by(Rule.FiveCards::enabled, false),
            by(Rule.FiveCards::payout, null),
            by(Rule.Insurance::enabled, true),
            by(Rule.Insurance::payout, 2.0),
            by(Rule.BustSideBet::enabled, true),
            by(Rule.BustSideBet::payout, 1.0),
            by(Rule.LuckyLuckySideBet::enabled, true),
            by(Rule.LuckyLuckySideBet::payout19,2.0),
            by(Rule.LuckyLuckySideBet::payout20,2.0),
            by(Rule.LuckyLuckySideBet::payout21,3.0),
            by(Rule.LuckyLuckySideBet::payout21Match, 15.0),
            by(Rule.LuckyLuckySideBet::payoutSequence, 30.0),
            by(Rule.LuckyLuckySideBet::payoutTripple, 50.0),
            by(Rule.LuckyLuckySideBet::payoutSequenceMatch, 100.0),
            by(Rule.LuckyLuckySideBet::payoutTrippleMatch, 200.0),
        ).associateBy { it.base }
    }

    fun <Type> of(base: Rule<Type>): StoredRule<Type> {
        @Suppress("UNCHECKED_CAST")
        return lookupMap[base] as StoredRule<Type>
    }

    fun saveable() = lookupMap.values.associate { it.name to it.json }

}