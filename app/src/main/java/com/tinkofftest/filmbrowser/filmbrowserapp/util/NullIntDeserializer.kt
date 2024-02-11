package com.tinkofftest.filmbrowser.filmbrowserapp.util

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class NullIntDeserializer : JsonDeserializer<Int?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): Int? {
        return if (json?.asString == "null") null else json?.asInt
    }
}