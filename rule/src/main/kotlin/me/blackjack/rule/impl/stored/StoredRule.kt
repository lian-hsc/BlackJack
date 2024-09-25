package me.blackjack.rule.impl.stored

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.blackjack.rule.Rule

internal class StoredRule<Type>(
    val name: String,
    val base: Rule<Type>,
    val serializer: KSerializer<Type>,
    value: JsonElement?,
    default: Type,
) {

    var value: Type =
        value
            ?.let { Json.decodeFromJsonElement(serializer, it) }
            ?: default

    fun setAnyValue(value: Any?) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as Type
    }

}