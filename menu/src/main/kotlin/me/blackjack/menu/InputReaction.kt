package me.blackjack.menu

sealed interface InputReaction

data class Message(val message: String?) : InputReaction

data object Redraw : InputReaction

data class Push(val menu: Menu, val pushType: MenuService.PushType = MenuService.PushType.PUSH) : InputReaction

data class Pop(val times: Int = 1) : InputReaction