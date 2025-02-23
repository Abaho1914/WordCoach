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
class NewViewModel @Inject constructor(
    private val repository: WordOfTheDayRepository
) : ViewModel() {
    private val _wordOfTheDayState = MutableStateFlow<WordOfTheDayState>(WordOfTheDayState.Loading)
    val wordOfTheDayState: StateFlow<WordOfTheDayState> = _wordOfTheDayState.asStateFlow()

        init {
            loadWord()
        }

        fun refresh() = viewModelScope.launch {
            fetchWord()
        }

        private fun loadWord() = viewModelScope.launch {
            fetchWord()
        }

        /**
         * Fetches the word of the day from the repository.
         *
         * This function retrieves the word of the day and updates the [_wordOfTheDayState] accordingly.
         * It first sets the state to [WordOfTheDayState.Loading], indicating that the word is being fetched.
         * Then, it attempts to fetch the word from the repository using [repository.fetchWordOfTheDay()].
         *
         * If the fetch is successful, the state is updated to [WordOfTheDayState.Success] with the fetched word.
         * If the fetch fails, the state is updated to [WordOfTheDayState.Error] with the error message.
         */
        private suspend fun fetchWord() {
            _wordOfTheDayState.value = WordOfTheDayState.Loading
            repository.fetchWordOfTheDay().fold(
                onSuccess = { _wordOfTheDayState.value = WordOfTheDayState.Success(it) },
                onFailure = { _wordOfTheDayState.value = WordOfTheDayState.Error(it.message.toString()) }
            )
        }

}

