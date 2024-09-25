package me.blackjack.bank

interface BankService {

    val capital: Long
    val loan: Long

    val availableCapital: Long
    val reservedCapital: Long

    fun reserve(amount: Long)
    fun free(amount: Long)
    fun collect(amount: Long)

    fun save()

}