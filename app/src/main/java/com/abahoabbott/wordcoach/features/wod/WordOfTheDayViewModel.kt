package com.abahoabbott.wordcoach.features.wod

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WordOfTheDayViewModel @Inject constructor() : ViewModel() {
    private val _wordOfTheDay = MutableStateFlow<WordOfTheDay?>(null)
    val wordOfTheDay: StateFlow<WordOfTheDay?> = _wordOfTheDay
}