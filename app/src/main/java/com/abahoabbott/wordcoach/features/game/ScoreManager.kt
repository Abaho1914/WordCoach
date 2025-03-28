package com.abahoabbott.wordcoach.features.game


/**
 * Manages the user's score and the number of passed questions
 */
data class ScoreState(
    val score: Int = 0,
    val correctQuestions: Int = 0
)

/**
 * Tracks the current question and the user's selected option
 */
data class QuestionState(
    val currentQuestion: WordQuestion = WordQuestion(),
    val selectedOptionId: Int? = null
)


/**
 * Tracks progress by keeping count of answered and total questions.
 */
data class GameProgress(
    val totalQuestions: Int = 5,
    val answeredQuestions: Int = 0
) {
    val progressFraction: Float
        get() = if (totalQuestions == 0) 0.0f else answeredQuestions.toFloat() / totalQuestions
}


