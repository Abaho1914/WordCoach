package com.abahoabbott.wordcoach.room

import androidx.room.TypeConverter
import com.abahoabbott.wordcoach.network.data.Definition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromDefinition(definition: Definition): String {
        return Json.encodeToString(definition)
    }

    @TypeConverter
    fun toDefinition(json: String): Definition {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromExamples(list: List<String>): String{
        return Gson().toJson(list) as String
    }

    @TypeConverter
    fun toExamples(string: String?): List<String>{
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string,listType)
    }

}