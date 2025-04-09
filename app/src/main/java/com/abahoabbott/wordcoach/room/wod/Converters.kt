package com.abahoabbott.wordcoach.room.wod

import androidx.room.TypeConverter
import com.abahoabbott.wordcoach.features.wod.Example
import com.abahoabbott.wordcoach.network.data.Definition
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromDefinition(definition: Definition): String {
        return Json.encodeToString(definition)
    }

    @TypeConverter
    fun toDefinition(json: String): Definition {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromExamples(examples: List<Example>): String{
        return json.encodeToString(examples)
    }

    @TypeConverter
    fun toExamples(string: String): List<Example>{
        return json.decodeFromString(string)
    }

}