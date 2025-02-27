package org.example.common.JsonUtil

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.nio.charset.Charset

object JsonUtil {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun <T> encodeToJson(data: T, serializer: KSerializer<T>): String {
        return json.encodeToString(serializer, data)
    }

    fun <T> decodeFromJson(jsonString: String, serializer: KSerializer<T>): T {
        return json.decodeFromString(serializer, jsonString)
    }

    fun encodeToBytes(value: Any): ByteArray {
        return json.encodeToString(value).toByteArray(Charsets.UTF_8)
    }

    fun parseToJsonElement(jsonString: String) = json.parseToJsonElement(jsonString)
}