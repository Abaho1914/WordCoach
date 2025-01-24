package com.abahoabbott.wordcoach.features.game

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.common.dataStore
import com.abahoabbott.wordcoach.data.GameUiState
import com.abahoabbott.wordcoach.features.results.AnswerState
import com.abahoabbott.wordcoach.features.results.QuestionResult
import com.abahoabbott.wordcoach.nav.NavEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the game logic and UI state.
 *
 * This class handles the flow of the game, including:
 * - Selecting questions.
 * - Tracking user progress and score.
 * - Updating the UI state.
 * - Navigating to the results screen.
 * - Resetting the game.
 */
@HiltViewModel
class GameViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {

    //Game Ui State
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    //set of questions to be used in the game
    private var usedQuestions: MutableList<WordQuestion> = mutableListOf()

    //set of questions that the user has answered
    private var questionResults: MutableList<QuestionResult> = mutableListOf()

    //Navigation event
    private val _events = Channel<NavEvent>()
    val events = _events.receiveAsFlow()

    init {
        resetGame()
    }


    fun onOptionSelected(selectedOptionId: Int, isCorrect: Boolean) {
        if (usedQuestions.size <= MAX_NO_OF_QUESTIONS) {
            updateUiState(selectedOptionId, isCorrect)
            proceedToNextStep()
            updateQuestionResults(isCorrect)
        }
    }

    private fun updateUiState(selectedOptionId: Int, isCorrect: Boolean) {
        _uiState.update { state ->
            state.copy(
                scoreState = state.scoreState.copy(
                    score = state.scoreState.score + if (isCorrect) SCORE_INCREASE else 0,
                    correctQuestions = state.scoreState.correctQuestions + if (isCorrect) 1 else 0
                ),
                questionState = state.questionState.copy(
                    selectedOptionId = selectedOptionId
                )
            )
        }
    }


    private fun proceedToNextStep() {
        viewModelScope.launch {
            if (usedQuestions.size == MAX_NO_OF_QUESTIONS) {
                //navigate to results screen if game is over
                navigateToResultsScreen()
            } else {
                //pick next question if the game is not yet over
                showNextQuestion()
            }

        }
    }

    private suspend fun showNextQuestion() {
        delay(5000L)
        _uiState.update { state ->
            state.copy(
                questionState = state.questionState.copy(
                    currentQuestion = pickNextQuestion(),
                    selectedOptionId = null
                ),
                gameProgress = state.gameProgress.copy(
                    answeredQuestions = questionResults.size,
                    totalQuestions = MAX_NO_OF_QUESTIONS
                )
            )
        }

    }

    private suspend fun navigateToResultsScreen() {
        //Navigate to results screen

        // Calculate the total score for the user: current game's score plus the cumulative score.
        val totalScore = _uiState.value.scoreState.score + getCumulativeScore()

        // Save the updated cumulative score to DataStore
        saveCumulativeScore(totalScore)

        val gameResult = GameResult(
            totalScore = totalScore,
            passedQuestions = _uiState.value.scoreState.correctQuestions,
            attemptedQuestions = questionResults
        )

        delay(1500L)

        _events.send(
            NavEvent.NavigateToResultsScreen(
                gameResult
            )
        )
    }

    private suspend fun getCumulativeScore(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[SCORE_KEY] ?: 0
        }
            .first()
    }

    private suspend fun saveCumulativeScore(score: Int) {
        context.dataStore.edit { preferences ->
            preferences[SCORE_KEY] = score
        }
    }

    private suspend fun resetCumulativeScore() {
        context.dataStore.edit { preferences ->
            preferences[SCORE_KEY] = 0
        }
    }

    private fun pickNextQuestion(): WordQuestion {
        val remainingQuestions = allQuestions.filter { it !in usedQuestions }
        return remainingQuestions.random().also { usedQuestions.add(it) }
    }


    fun resetGame() {
        clearGameState()
        viewModelScope.launch {
            _uiState.value = GameUiState(
                questionState = QuestionState(currentQuestion = pickNextQuestion()),
                scoreState = ScoreState(
                    score = getCumulativeScore(),
                    correctQuestions = 0,
                ),
                gameProgress = GameProgress(
                    totalQuestions = MAX_NO_OF_QUESTIONS,
                )
            )
        }


    }

    private fun clearGameState() {
        usedQuestions.clear()
        questionResults.clear()
    }

    private fun updateQuestionResults(isCorrect: Boolean) {
        val answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.WRONG
        val questionResult = QuestionResult(
            question = _uiState.value.questionState.currentQuestion.question,
            answerState = answerState
        )
        questionResults.add(questionResult)
    }


    /**
     * When user skips a question
     */
    fun onSkip() {
        //Update answer state to unanswered
        val questionResult =
            QuestionResult(
                _uiState.value.questionState.currentQuestion.question,
                AnswerState.UNANSWERED
            )
        questionResults.add(questionResult)
        //
        proceedToNextStep()
    }


    override fun onCleared() {
        viewModelScope.launch {
            resetCumulativeScore()
        }

        super.onCleared()
    }

    companion object {
        //Score increase when user answers correctly
        private const val SCORE_INCREASE = 120

        //Max number of questions are user can attempt per game
        private const val MAX_NO_OF_QUESTIONS = 5

        private val SCORE_KEY = intPreferencesKey("cumulative_score")
    }

}