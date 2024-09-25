package me.blackjack.rule.impl

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.blackjack.rule.Rule
import me.blackjack.rule.impl.stored.StoredRuleService
import org.koin.core.annotation.Single
import java.io.File
import me.blackjack.rule.RuleService as IRuleService

@Single
@OptIn(ExperimentalSerializationApi::class)
internal class RuleService(
    val storedRuleService: StoredRuleService,
) : IRuleService {

    private val rulesFiles = File(".", "blackjack-rules.json")

    init {
        if (!rulesFiles.exists()) {
            rulesFiles.parentFile.mkdirs()
            rulesFiles.createNewFile()
            rulesFiles.writeText("{}")
        }

        val loaded = Json.decodeFromStream<Map<String, JsonElement>>(rulesFiles.inputStream())
        storedRuleService.initLookupMap(loaded)
    }

    override fun <T> getValue(rule: Rule<T>): T {
        return storedRuleService.of(rule).value
    }

    override fun <T> setValue(rule: Rule<T>, value: T) {
        storedRuleService.of(rule).value = value
    }

    internal fun setAnyValue(rule: Rule<*>, value: Any?) {
        storedRuleService.of(rule).setAnyValue(value)
    }

    override fun saveRules() {
        Json.encodeToStream(storedRuleService.all(), rulesFiles.outputStream())
    }

}