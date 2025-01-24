package me.blackjack.menu

import me.blackjack.terminal.rgb

fun Any.selectorIf(condition: Boolean, highlight: Boolean = true) =
    if (condition) " < $this > ".let { if (highlight) it.rgb(255, 255, 85) else it }
    else this.toString()

fun Any.highlightIf(condition: Boolean) = if (condition) this.toString().rgb(255, 255, 85) else this.toString()

fun Any.highlight() = this.toString().rgb(255, 255, 85)