package com.abahoabbott.wordcoach.features.wod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.network.WordnikApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

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

    private fun checkAndFetchWordOfTheDay() {
        viewModelScope.launch {
            val lastFetchTime = dataStoreManager.lastFetchTime.first()
            val cachedWord = dataStoreManager.lastWord.first()

            if (isSameDay(lastFetchTime) && cachedWord != null) {
                // Use the cached word instead of making a network request
                _wordOfTheDayState.value = WordOfTheDayState.Success(cachedWord)
            } else {
                fetchWordOfTheDay()
            }
        }
    }

    private fun fetchWordOfTheDay() {
        viewModelScope.launch {
            _wordOfTheDayState.value = WordOfTheDayState.Loading
            try {
                val response = wordnikApiService.getWordOfTheDay()
                val wordOfTheDay = response.toWordOfTheDay()

                _wordOfTheDayState.value = WordOfTheDayState.Success(wordOfTheDay)

                // Save the word and fetch timestamp
                dataStoreManager.saveWordOfTheDay(wordOfTheDay)
                dataStoreManager.saveLastFetchTime(System.currentTimeMillis())

            } catch (e: Exception) {
                _wordOfTheDayState.value =
                    WordOfTheDayState.Error("Failed to fetch word of the day: ${e.message}")
            }
        }
    }
    private fun isSameDay(lastFetchTime: Long): Boolean {
        val lastDate = Calendar.getInstance().apply { timeInMillis = lastFetchTime }
        val currentDate = Calendar.getInstance()

        return lastDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                lastDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
    }


}


sealed class WordOfTheDayState() {
    object Loading : WordOfTheDayState()
    data class Success(val wordOfTheDay: WordOfTheDay) : WordOfTheDayState()
    data class Error(val message: String) : WordOfTheDayState()
}