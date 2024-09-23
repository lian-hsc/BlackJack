package me.blackjack.menu

interface MenuService {

    val stack: List<Menu>

    fun push(menu: Menu, pushType: PushType = PushType.PUSH)

    fun pop(times: Int = 1)

    enum class PushType {
        PUSH,
        REPLACE,
    }

}