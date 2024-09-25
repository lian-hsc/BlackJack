package me.blackjack.rule

interface RuleService {

    fun <T> getValue(rule: Rule<T>): T

    fun <T> setValue(rule: Rule<T>, value: T)

    fun saveRules()

}