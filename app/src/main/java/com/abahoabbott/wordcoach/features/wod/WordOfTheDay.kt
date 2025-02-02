package com.abahoabbott.wordcoach.features.wod

import com.abahoabbott.wordcoach.network.data.WordOfTheDayResponse

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