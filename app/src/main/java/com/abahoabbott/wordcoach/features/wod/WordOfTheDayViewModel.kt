package com.abahoabbott.wordcoach.features.wod


/**
 * Represents the state of word of the day data loading.
 */
sealed class WordOfTheDayState {
    object Loading : WordOfTheDayState()
    data class Success(
        val wordOfTheDay: WordOfTheDay,
    ) : WordOfTheDayState()

    data class Error(
        val message: String,
    ) : WordOfTheDayState()
}

/**
 * Categorizes error types for better error handling and analytics.
 */