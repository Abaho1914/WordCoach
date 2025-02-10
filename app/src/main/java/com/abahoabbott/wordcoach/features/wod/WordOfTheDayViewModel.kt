package com.abahoabbott.wordcoach.features.wod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.network.WordnikApiService
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _wordOfTheDayState = MutableStateFlow<WordOfTheDayState>(WordOfTheDayState.Loading)
    val wordOfTheDayState: StateFlow<WordOfTheDayState> = _wordOfTheDayState

    init {
        checkAndFetchWordOfTheDay()
    }

    /**
     * Checks cached data and fetches new word if needed.
     * Optimizes network usage by checking cached data validity first.
     */
    private fun checkAndFetchWordOfTheDay() {
        viewModelScope.launch {
            try {
                // Combine both data store requests into a single operation
                val (lastFetchTime, cachedWord) = combine(
                    dataStoreManager.lastFetchTime,
                    dataStoreManager.lastWord
                ) { time, word -> Pair(time, word) }.first()

                // Check if the cached word is from today and if the API has updated the WOD
                if (isSameDay(lastFetchTime) && cachedWord != null && !isApiUpdateTimePassed()) {
                    // Use cached word if it's from today and the API update time hasn't passed
                    _wordOfTheDayState.value = WordOfTheDayState.Success(cachedWord)
                } else {
                    // Fetch new word if the cached word is outdated or the API update time has passed
                    fetchWordOfTheDay()
                }
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
    private fun fetchWordOfTheDay() {
        viewModelScope.launch {
            _wordOfTheDayState.value = WordOfTheDayState.Loading
            try {
                val response = wordnikApiService.getWordOfTheDay()
                val wordOfTheDay = response.toWordOfTheDay()

                // Update state before saving to cache to ensure UI responsiveness
                _wordOfTheDayState.value = WordOfTheDayState.Success(wordOfTheDay)

                // Atomic save operation for both word and timestamp
                dataStoreManager.saveWordData(
                    word = wordOfTheDay,
                    timestamp = System.currentTimeMillis()
                )

            } catch (e: IOException) {
                // If network fails, try to use the cached word if available
                val (_, cachedWord) = combine(
                    dataStoreManager.lastFetchTime,
                    dataStoreManager.lastWord
                ) { time, word -> Pair(time, word) }.first()

                if (cachedWord != null) {
                    _wordOfTheDayState.value = WordOfTheDayState.Success(cachedWord)
                } else {
                    _wordOfTheDayState.value = WordOfTheDayState.Error(
                        message = "Network error: ${e.localizedMessage}",
                        type = ErrorType.NETWORK_ERROR
                    )
                }
            } catch (e: Exception) {
                _wordOfTheDayState.value = WordOfTheDayState.Error(
                    message = "Unexpected error: ${e.localizedMessage}",
                    type = ErrorType.GENERIC_ERROR
                )
            }
        }
    }

    /**
     * Checks if the given timestamp is from the same calendar day as current time.
     *
     * @param timestamp The timestamp to check in milliseconds
     * @return true if the timestamp is from today, false otherwise
     */
    private fun isSameDay(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.apply { timeInMillis = System.currentTimeMillis() }

        return calendar.run {
            timeInMillis = timestamp
            currentDay.get(Calendar.YEAR) == get(Calendar.YEAR) &&
                    currentDay.get(Calendar.DAY_OF_YEAR) == get(Calendar.DAY_OF_YEAR)
        }
    }

    /**
     * Checks if the API update time for the Word of the Day has passed.
     *
     * @return true if the API update time has passed, false otherwise
     */
    private fun isApiUpdateTimePassed(): Boolean {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Assuming the API updates the WOD at 12:00 PM (noon)
        val apiUpdateHour = 7
        val apiUpdateMinute = 0

        return currentHour > apiUpdateHour || (currentHour == apiUpdateHour && currentMinute >= apiUpdateMinute)
    }
}

/**
 * Represents the state of word of the day data loading.
 */
sealed class WordOfTheDayState {
    object Loading : WordOfTheDayState()
    data class Success(val wordOfTheDay: WordOfTheDay) : WordOfTheDayState()
    data class Error(val message: String, val type: ErrorType) : WordOfTheDayState()
}

/**
 * Categorizes error types for better error handling and analytics.
 */
enum class ErrorType {
    NETWORK_ERROR,
    LOCAL_DATA_ERROR,
    GENERIC_ERROR
}