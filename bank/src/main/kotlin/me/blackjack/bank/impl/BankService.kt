package me.blackjack.bank.impl

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.annotation.Single
import java.io.File
import me.blackjack.bank.BankService as IBankService

@Single
internal class BankService : IBankService {

    private val bankFile = File(".", "blackjack-bank.json")

    override var capital: Long
    override var loan: Long

    override var reservedCapital: Long = 0

    override val availableCapital: Long
        get() = capital - reservedCapital


    init {
        if (!bankFile.exists()) {
            bankFile.parentFile.mkdirs()
            bankFile.createNewFile()

            @Suppress("OPT_IN_USAGE")
            Json.encodeToStream(StoredBank(0, 0), bankFile.outputStream())
        }

        @Suppress("OPT_IN_USAGE")
        val storedBank = Json.decodeFromStream<StoredBank>(bankFile.inputStream())

        capital = storedBank.capital
        loan = storedBank.loan
    }

    override fun reserve(amount: Long) {
        if (amount > availableCapital) {
            throw IllegalStateException("Not enough capital to reserve $amount")
        }

        reservedCapital += amount
    }

    override fun free(amount: Long) {
        if (amount > reservedCapital) {
            throw IllegalStateException("Not enough reserved capital to free $amount")
        }

        reservedCapital -= amount
    }

    override fun remove(amount: Long) {
        if (amount > capital) {
            throw IllegalStateException("Not enough capital to collect $amount")
        }

        capital -= amount
    }

    override fun add(amount: Long) {
        capital += amount
    }

    fun getLoan(amount: Long) {
        loan += amount
        capital += amount
    }

    fun repayLoan(amount: Long) {
        if (amount > loan) {
            throw IllegalStateException("Not enough loan to repay $amount")
        }

        if (amount > availableCapital) {
            throw IllegalStateException("Not enough capital to repay $amount")
        }

        loan -= amount
        capital -= amount
    }

    override fun save() {
        @Suppress("OPT_IN_USAGE")
        Json.encodeToStream(StoredBank(capital, loan), bankFile.outputStream())
    }

    @Serializable
    data class StoredBank(
        val capital: Long,
        val loan: Long,
    )

}