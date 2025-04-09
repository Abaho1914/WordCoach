package com.abahoabbott.wordcoach.features.wod.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.features.wod.WordOfTheDayState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class WordOfTheDayViewModel @Inject constructor(
    private val repository: WordOfTheDayRepository
) : ViewModel() {
    private val _wordOfTheDayState = MutableStateFlow<WordOfTheDayState>(WordOfTheDayState.Loading)
    val wordOfTheDayState: StateFlow<WordOfTheDayState> = _wordOfTheDayState.asStateFlow()

    /**
     * Initialize by loading the word of the day from the database first.
     * This avoids unnecessary network calls on every initialization.
     */
    init {
        loadWord()
    }

    /**
     * Loads today's word, preferring database cache if available.
     */
    private fun loadWord() = viewModelScope.launch {
        _wordOfTheDayState.value = WordOfTheDayState.Loading
        repository.getLatestWordFromDatabase().fold(
            onSuccess = { _wordOfTheDayState.value = WordOfTheDayState.Success(it) },
            onFailure = { _wordOfTheDayState.value = WordOfTheDayState.Error(it.message ?: "Unknown error") }
        )
    }

    /**
     * Explicitly refreshes the word of the day from the network.
     * This should be called only when user requests a refresh.
     */
    fun refresh() = viewModelScope.launch {
        _wordOfTheDayState.value = WordOfTheDayState.Loading
        repository.forceRefreshTodayWord().fold(
            onSuccess = { _wordOfTheDayState.value = WordOfTheDayState.Success(it) },
            onFailure = { _wordOfTheDayState.value = WordOfTheDayState.Error(it.message ?: "Unknown error") }
        )
    }
}