package com.abahoabbott.wordcoach.features.results

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ResultsViewModel(): ViewModel(){

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState


    fun updateResults(
        score: Int,
        correctAnswers: Int,
        questionResults: List<QuestionResult>
    ){
        _uiState.value  = ResultsUiState(
            score = score,
            correctAnswers = correctAnswers,
            attemptedQuestions = questionResults,
            totalQuestions = 5
        )
    }

    fun playAgain(){

    }

}