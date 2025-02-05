package com.abahoabbott.wordcoach.features.wod

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
    val word: String,
    val pronunciation: String,
    val definition: String,
    val examples: List<String>
)


fun WordOfTheDayResponse.toWordOfTheDay(): WordOfTheDay {
    return WordOfTheDay(
        word = this.word,
        pronunciation = "/ˈbalzəˌrin/", // Mocked, as Wordnik doesn't provide this
        definition = this.definitions.firstOrNull()?.text ?: "No definition available",
        examples = this.examples.map { it.text }
    )
}