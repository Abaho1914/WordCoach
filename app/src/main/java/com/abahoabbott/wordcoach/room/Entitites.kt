package com.abahoabbott.wordcoach.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abahoabbott.wordcoach.features.wod.Example
import com.abahoabbott.wordcoach.features.wod.WordOfTheDay
import com.abahoabbott.wordcoach.network.data.Definition
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "word_of_the_day")
data class WordOfTheDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Auto-generated ID for primary key
    val word: String,
    val pronunciation: String,
    val definition: Definition, // Will be stored as a JSON string using TypeConverter
    val examples: List<Example>,
    var publishDate: String,
)


fun WordOfTheDay.toEntity(
): WordOfTheDayEntity{
    return WordOfTheDayEntity(
        word = this.word,
        pronunciation = this.pronunciation,
        definition =this.definition,
        examples = this.examples,
        publishDate = this.publishDate
    )

}


