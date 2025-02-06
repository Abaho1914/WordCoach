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
    val word: String,
    val pronunciation: String,
    val definition: Definition,
    val examples: List<String>,
)


fun WordOfTheDayResponse.toWordOfTheDay(): WordOfTheDay {
    return WordOfTheDay(
        word = this.word,
        pronunciation = "[ˈbalzəˌrin]",
        definition =this.definitions.first(),
        examples = this.examples.map { it.text },
    )
}