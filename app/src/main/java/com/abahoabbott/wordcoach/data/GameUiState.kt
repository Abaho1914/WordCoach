package com.abahoabbott.wordcoach.data

import com.abahoabbott.wordcoach.features.game.WordQuestion

/**
 * Data class that represents the game UI state.
 *
 * This class holds the state of the game's user interface, including the current question,
 * the player's score, selected option, game over status, progress, and the number of passed questions.
 *
 * @property currentQuestion The currently displayed question of type [WordQuestion]. Defaults to an empty [WordQuestion].
 * @property score The player's current score, represented as an [Int]. Defaults to 0.
 * @property selectedOptionId The ID of the option selected by the player for the current question, or `null` if no option is selected. Represented as an nullable [Int].
 * @property isGameOver A boolean indicating whether the game is over or not. Defaults to `false`.
 * @property progress A float value representing the game's progress, typically between 0.0f and 1.0f. Defaults to 0.0f.
 * @property passedQuestions The number of questions the player has successfully passed. Represented as an [Int]. Defaults to 0.
 */
data class GameUiState(
    val currentQuestion: WordQuestion = WordQuestion(),
    val score: Int = 0,
    val selectedOptionId: Int? = null,
    val isGameOver: Boolean = false,
    val progress: Float = 0.0f,
    val passedQuestions: Int =0
)

