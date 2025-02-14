package com.abahoabbott.wordcoach.features.wod


/**
 * Represents the state of word of the day data loading.
 */
sealed class WordOfTheDayState {
    object Loading : WordOfTheDayState()
    data class Success(
        val wordOfTheDay: WordOfTheDay,
        val isFromCache: Boolean,
        val lastUpdated: Long
    ) : WordOfTheDayState()

    data class Error(
        val message: String,
        val type: ErrorType,
        val cachedData: WordOfTheDay? = null
    ) : WordOfTheDayState()
}

/**
 * Categorizes error types for better error handling and analytics.
 */
enum class ErrorType {
    NETWORK_ERROR,
    LOCAL_DATA_ERROR,
    GENERIC_ERROR
}