package com.abahoabbott.wordcoach.features.wod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.network.WordnikApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel responsible for managing the Word of the Day data.
 * Handles data fetching, caching, and business logic for the Word of the Day feature.
 *
 * @param wordnikApiService API service for fetching word data
 * @param dataStoreManager Data storage manager for caching word and timestamp
 */
@HiltViewModel
class WordOfTheDayViewModel @Inject constructor(
    private val wordnikApiService: WordnikApiService,
    private val dataStoreManager: DataStoreManager,
    private val timeProvider: TimeProvider,
    private val config: WordOfDayConfig = WordOfDayConfig()
) : ViewModel() {

    private val _wordOfTheDayState = MutableStateFlow<WordOfTheDayState>(WordOfTheDayState.Loading)
    val wordOfTheDayState: StateFlow<WordOfTheDayState> = _wordOfTheDayState


    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _wordOfTheDayState.value = WordOfTheDayState.Error(
            message = throwable.localizedMessage ?: "Unknown error occurred",
            type = when (throwable) {
                is IOException -> ErrorType.NETWORK_ERROR
                is IllegalStateException -> ErrorType.LOCAL_DATA_ERROR
                else -> ErrorType.GENERIC_ERROR
            }
        )
    }

    init {
        checkAndFetchWordOfTheDay()
    }

    /**
     * Implements cache strategy logic
     */
    private suspend fun handleCacheStrategy() {
        val currentTime = timeProvider.getCurrentTimeMillis()

        val (lastFetchTime, cachedWord) = combine(
            dataStoreManager.lastFetchTime,
            dataStoreManager.lastWord
        ) { time, word -> Pair(time, word) }.first()

        when {
            isCacheValid(lastFetchTime, currentTime) &&
                    cachedWord != null &&
                    !isApiUpdateTimePassed() -> {
                _wordOfTheDayState.value = WordOfTheDayState.Success(
                    wordOfTheDay = cachedWord,
                    isFromCache = true,
                    lastUpdated = lastFetchTime
                )

            }

            else -> {
                fetchWordOfTheDay()
            }
        }
    }

    /**
     * Checks cached data and fetches new word if needed.
     */
    private fun checkAndFetchWordOfTheDay() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                handleCacheStrategy()
            } catch (e: Exception) {
                _wordOfTheDayState.value = WordOfTheDayState.Error(
                    message = "Failed to load cached data: ${e.localizedMessage}",
                    type = ErrorType.LOCAL_DATA_ERROR
                )

            }
        }
    }

    /**
     * Fetches the word of the day from the API and updates cache.
     * Handles network errors and data parsing errors separately.
     */
    private fun fetchWordOfTheDay(retryCount: Int = 3) {
        viewModelScope.launch(coroutineExceptionHandler) {
            var lastException: Exception? = null
            repeat(retryCount) { attempt ->
                try {
                    val response = wordnikApiService.getWordOfTheDay()
                    val wordOfTheDay = response.toWordOfTheDay()
                    _wordOfTheDayState.value = WordOfTheDayState.Success(
                        wordOfTheDay = wordOfTheDay,
                        isFromCache = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                    return@launch
                } catch (e: Exception) {
                    lastException = e
                    if (attempt < retryCount - 1) {
                        delay(1000L * (attempt + 1))
                    }
                }
            }
            handleFetchFailure(lastException)
        }
    }

    /**
     * Handles API fetch failures by attempting to use cached data
     */
    private suspend fun handleFetchFailure(exception: Exception?) {
        val (_, cachedWord) = combine(
            dataStoreManager.lastFetchTime,
            dataStoreManager.lastWord
        ) { time, word -> Pair(time, word) }.first()

        if (cachedWord != null) {
            _wordOfTheDayState.value = WordOfTheDayState.Success(
                wordOfTheDay = cachedWord,
                isFromCache = true,
                lastUpdated = timeProvider.getCurrentTimeMillis()
            )

        } else {
            _wordOfTheDayState.value = WordOfTheDayState.Error(
                message = "Failed to fetch word: ${exception?.localizedMessage}",
                type = when (exception) {
                    is IOException -> ErrorType.NETWORK_ERROR
                    else -> ErrorType.GENERIC_ERROR
                },
                cachedData = null
            )
        }
    }

    private fun isCacheValid(lastFetchTime: Long, currentTime: Long): Boolean =
        currentTime - lastFetchTime < config.cacheThresholdMs

    private fun isApiUpdateTimePassed(): Boolean {
        val calendar = timeProvider.getCurrentCalendar()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        return currentHour > config.apiUpdateHour ||
                (currentHour == config.apiUpdateHour && currentMinute >= config.apiUpdateMinute)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}


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