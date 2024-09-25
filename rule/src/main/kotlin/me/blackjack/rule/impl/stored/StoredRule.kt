package me.blackjack.rule.impl.stored

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import me.blackjack.rule.Rule
import kotlin.reflect.KType

internal class StoredRule<Type>(
    val name: String,
    val base: Rule<Type>,
    private val serializer: KSerializer<Type>,
    value: JsonElement?,
    default: Type,
) {

    var value: Type =
        value
            ?.let { Json.decodeFromJsonElement(serializer, it) }
            ?: default

    val json: JsonElement
        get() = Json.encodeToJsonElement(serializer, value)

    fun setAnyValue(value: Any?) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as Type
    }

}