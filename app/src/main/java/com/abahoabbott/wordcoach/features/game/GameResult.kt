package com.abahoabbott.wordcoach.features.game

import com.abahoabbott.wordcoach.features.results.QuestionResult
import kotlinx.serialization.Serializable


@Serializable
data class GameResult(
    val resultId: Int = 0,
    val totalScore: Int = 0,
    val passedQuestions: Int = 0,
    val attemptedQuestions: List<QuestionResult> = emptyList()
)