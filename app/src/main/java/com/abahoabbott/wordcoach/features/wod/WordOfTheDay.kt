package com.abahoabbott.wordcoach.features.wod

import com.abahoabbott.wordcoach.network.data.Definition
import com.abahoabbott.wordcoach.network.data.WordOfTheDayResponse
import kotlinx.serialization.Serializable


/**
 * Data class representing the Word of the Day.
 *
 * @property word The word.
 * @property definition The definition of the word.
 * @property examples A list of examples.
 */
@Serializable
data class WordOfTheDay(
    val apiId: String,
    val word: String,
    val pronunciation: String,
    val definition: Definition,
    val examples: List<Example>,
    val publishDate: String,
    val note: String
)

@Serializable
data class Example(
    val text: String,
    val title: String
)

/**
 * Converts a [WordOfTheDayResponse] network response to a [WordOfTheDay] domainModel
 */
fun WordOfTheDayResponse.toWordOfTheDay(): WordOfTheDay {
    return WordOfTheDay(
        apiId = this._id,
        word = this.word,
        pronunciation = "[ˈbalzəˌrin]",
        definition = this.definitions.first(),
        examples = this.examples.map { Example(it.text, it.title) },
        publishDate = this.publishDate,
        note = this.note
    )
}

