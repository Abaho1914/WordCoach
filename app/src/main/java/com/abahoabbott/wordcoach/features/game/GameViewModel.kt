package com.abahoabbott.wordcoach.features.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onOptionSelected(optionId: Int) {
        _uiState.update { it.copy(selectedOptionId = optionId) }
    }

    fun onSkip() {
        // Handle skip logic
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate loading next question
            _uiState.update { currentState ->
                currentState.copy(
                    progress = (currentState.progress + 0.1f).coerceAtMost(1f),
                    isLoading = false
                )
            }
        }
    }

}