package com.abahoabbott.wordcoach.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.abahoabbott.wordcoach.network.data.Definition
import com.abahoabbott.wordcoach.network.data.Example
import kotlinx.serialization.Serializable



@Serializable
@Entity(tableName = "word_of_the_day")
data class WordOfTheDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Auto-generated ID for primary key
    val word: String,
    val pronunciation: String,
    val definition: Definition, // Will be stored as a JSON string using TypeConverter
    val examples: List<String>,
    val publishDate: String,
)



