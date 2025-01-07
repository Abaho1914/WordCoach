package com.abahoabbott.wordcoach.data

import com.abahoabbott.wordcoach.features.game.WordQuestion

/*
 *  Data class that represents the game UI state
 */
data class GameUiState(
    val currentQuestion: WordQuestion = WordQuestion(),
    val currentQuestionCount: Int = 1,
    val score: Int = 0,
    val selectedOptionId: Int? = null,
    val isGameOver: Boolean = false,
    val progress: Float = 0.0f,
    val passedQuestions: Int =0
)

