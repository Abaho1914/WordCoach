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
class GameViewModel @Inject constructor(@ApplicationContext private val context: Context) : ViewModel() {

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
            showNextQuestion(usedQuestions.size == MAX_NO_OF_QUESTIONS)
            updateQuestionResult(_uiState.value.currentQuestion.question, isCorrect)
        }
    }

    private fun updateUiState(selectedOptionId: Int, isCorrect: Boolean) {
        _uiState.update { state ->
            state.copy(
                isGameOver = false,
                selectedOptionId = selectedOptionId,
                score = state.score + if (isCorrect) SCORE_INCREASE else 0,
                passedQuestions = state.passedQuestions + if (isCorrect) 1 else 0,
            )
        }
    }


    private fun showNextQuestion(isGameOver: Boolean) {
        viewModelScope.launch {
            if (isGameOver) {
                //navigate to results screen if game is over
                navigateToResultsScreen()
            } else {
                //pick next question if the game is not yet over
                pickNextQuestion()
            }

        }
    }

    private suspend fun pickNextQuestion() {
        delay(1500L)
        _uiState.update { state ->
            state.copy(
                currentQuestion = pickRandomQuestionAndShuffle(),
                selectedOptionId = null,
            )
        }
        updateProgressBar()
    }

    private suspend fun navigateToResultsScreen() {
        //Navigate to results screen

        val cumulativeScore = getCumulativeScore()

        // Calculate the total score for the user: current game's score plus the cumulative score.
        val totalScore = _uiState.value.score + cumulativeScore

        // Save the updated cumulative score to DataStore
        saveCumulativeScore(totalScore)


        val gameResult = GameResult(
            totalScore = totalScore,
            passedQuestions = _uiState.value.passedQuestions,
            attemptedQuestions = questionResults
        )

        delay(1500L)
        updateProgressBar()
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


    fun resetGame() {
        usedQuestions.clear()
        questionResults.clear()
        viewModelScope.launch {
            _uiState.value = GameUiState(
                currentQuestion = pickRandomQuestionAndShuffle(),
                score = getCumulativeScore(),
                progress = 0f
            )
        }


    }

    private fun pickRandomQuestionAndShuffle(): WordQuestion {
        var question: WordQuestion
        while (true) {
            question = allQuestions.random()
            if (!usedQuestions.contains(question)) break
        }
        usedQuestions.add(question)
        return question
    }

    private fun updateProgressBar() {
        _uiState.update { gameUiState ->
            gameUiState.copy(
                progress = questionResults.size.toFloat() / MAX_NO_OF_QUESTIONS
            )
        }
    }

    private fun updateQuestionResult(questionText: String, isCorrect: Boolean) {
        val answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.WRONG
        val questionResult = QuestionResult(
            question = questionText,
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
            QuestionResult(_uiState.value.currentQuestion.question, AnswerState.UNANSWERED)
        questionResults.add(questionResult)
        //
        showNextQuestion(usedQuestions.size == MAX_NO_OF_QUESTIONS)
    }


    companion object {
        //Score increase when user answers correctly
        private const val SCORE_INCREASE = 120

        //Max number of questions are user can attempt per game
        private const val MAX_NO_OF_QUESTIONS = 5

        //Initial score for the game
        private const val INITIAL_SCORE = 0

        private val SCORE_KEY = intPreferencesKey("cumulative_score")
    }

}