package com.abahoabbott.wordcoach.data

import com.abahoabbott.wordcoach.features.game.Difficulty
import com.abahoabbott.wordcoach.features.game.GameProgress
import com.abahoabbott.wordcoach.features.game.QuestionState
import com.abahoabbott.wordcoach.features.game.ScoreState


data class GameUiState(
    val questionState: QuestionState = QuestionState(),
    val scoreState: ScoreState = ScoreState(),
    val gameProgress: GameProgress = GameProgress(),
    val isGameOver: Boolean = false,
    val difficulty: Difficulty = Difficulty.EASY
)

