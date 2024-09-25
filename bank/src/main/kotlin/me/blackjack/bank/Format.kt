package me.blackjack.bank

import org.koin.core.annotation.Single
import java.text.NumberFormat

fun Long.format(): String = NumberFormat.getInstance().format(this) + " $"