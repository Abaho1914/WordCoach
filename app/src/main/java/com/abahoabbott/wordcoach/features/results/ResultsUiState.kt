package com.abahoabbott.wordcoach.features.results

import kotlinx.serialization.Serializable


data class ResultsUiState(
    val score: Int = 0,
    val correctAnswers: Int = 1,
    val totalQuestions: Int = 5,
    val attemptedQuestions: List<QuestionResult> = emptyList()
)


@Serializable
data class QuestionResult(
    val questionId: Int,
    val answerState: AnswerState
)

@Serializable
enum class AnswerState() {
    CORRECT,
    WRONG,
    UNANSWERED
}