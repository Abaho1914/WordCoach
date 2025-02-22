package com.abahoabbott.wordcoach.features.wod

import com.abahoabbott.wordcoach.network.data.Definition
import com.abahoabbott.wordcoach.network.data.WordOfTheDayResponse
import kotlinx.serialization.Serializable
import java.util.Calendar
import javax.inject.Inject


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
    val examples: List<Example>,
    val publishDate: String,
    val note: String
)

@Serializable
data class Example(
    val text: String,
    val title: String
)

fun WordOfTheDayResponse.toWordOfTheDay(): WordOfTheDay {
    return WordOfTheDay(
        word = this.word,
        pronunciation = "[ˈbalzəˌrin]",
        definition = this.definitions.first(),
        examples =this.examples.map { Example(it.text, it.title) },
        publishDate = this.publishDate,
        note = this.note
    )
}


/**
 * Configuration for Word of the Day feature
 */
data class WordOfDayConfig(
    val apiUpdateHour: Int = 7,
    val apiUpdateMinute: Int = 0,
    val cacheThresholdMs: Long = 24 * 60 * 60 * 1000L, // 24 hours
    val maxRetries: Int = 3
)

/**
 * Interface for providing time-related functionality
 */
interface TimeProvider {
    fun getCurrentTimeMillis(): Long
    fun getCurrentCalendar(): Calendar
}

class RealTimeProvider @Inject constructor() : TimeProvider {
    override fun getCurrentTimeMillis() = System.currentTimeMillis()
    override fun getCurrentCalendar(): Calendar = Calendar.getInstance()
}

