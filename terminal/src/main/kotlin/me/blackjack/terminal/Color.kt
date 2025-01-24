package me.blackjack.terminal

import me.blackjack.terminal.impl.constant.Ansi

fun Any.rgb(r: Int, g: Int, b: Int) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.foreground(r, g, b)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_FOREGROUND}"

fun Any.rgb(hex: String) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.foreground(hex)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_FOREGROUND}"

fun Any.bg(r: Int, g: Int, b: Int) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.background(r, g, b)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_BACKGROUND}"

fun Any.bg(hex: String) =
    "${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.background(hex)}$this${Ansi.ESCAPE_SEQUENCE}${Ansi.Color.RESET_BACKGROUND}"