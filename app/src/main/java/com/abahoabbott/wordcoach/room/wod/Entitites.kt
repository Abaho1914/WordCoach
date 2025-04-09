package com.abahoabbott.wordcoach.room.wod

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
    val apiId: String,
    val word: String,
    val pronunciation: String,
    val definition: Definition, // Will be stored as a JSON string using TypeConverter
    val examples: List<Example>,
    val publishDate: String,
    val note: String
)


/**
 * Converts a [WordOfTheDay] to a [WordOfTheDayEntity]
 */

fun WordOfTheDay.toEntity(
): WordOfTheDayEntity {
    return WordOfTheDayEntity(
        apiId = this.apiId,
        word = this.word,
        pronunciation = this.pronunciation,
        definition = this.definition,
        examples = this.examples,
        publishDate = this.publishDate,
        note = this.note
    )

}

/**
 * Converts a [WordOfTheDayEntity] database model to a domain model [WordOfTheDay]
 */
fun WordOfTheDayEntity.toDomainModel(): WordOfTheDay {
    return WordOfTheDay(
        apiId = this.apiId,
        word = this.word,
        pronunciation = this.pronunciation,
        definition = this.definition,
        examples = this.examples,
        publishDate = this.publishDate,
        note = this.note
    )

}