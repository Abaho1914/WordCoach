package com.abahoabbott.wordcoach.features.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.data.GameUiState
import com.abahoabbott.wordcoach.features.results.AnswerState
import com.abahoabbott.wordcoach.features.results.QuestionResult
import com.abahoabbott.wordcoach.nav.NavEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel() : ViewModel() {

    //Game Ui State
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    //set of questions to be used in the game
    private var usedQuestions: MutableList<WordQuestion> = mutableListOf()

    private var questionResults: MutableList<QuestionResult> = mutableListOf()

    private val _events = Channel<NavEvent>()
    val events = _events.receiveAsFlow()

    init {
        resetGame()
    }


    fun onOptionSelected(selectedOptionId: Int, isCorrect: Boolean) {
        Log.i("Sunflower:GameViewModel", usedQuestions.size.toString())
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
                currentQuestionCount = state.currentQuestionCount + 1,
                score = state.score + if (isCorrect) SCORE_INCREASE else 0,
                passedQuestions = state.passedQuestions + if (isCorrect) 1 else 0
            )
        }
    }


    private fun showNextQuestion(isGameOver: Boolean) {
        viewModelScope.launch {
            if (isGameOver) {
                navigateToResultsScreen()
            } else {
                //pick next question
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
                progress = (state.currentQuestionCount).toFloat() / MAX_NO_OF_QUESTIONS
            )
        }
    }

    private suspend fun navigateToResultsScreen() {
        //Navigate to results screen

        val gameResult = GameResult(
            totalScore = _uiState.value.score,
            passedQuestions = _uiState.value.passedQuestions,
            attemptedQuestions = questionResults
        )
        delay(1500L)
        _events.send(
            NavEvent.NavigateToResultsScreen(
                gameResult
            )
        )
    }


    fun resetGame() {
        usedQuestions.clear()
        _uiState.value = GameUiState(
            score = INITIAL_SCORE,
            currentQuestion = pickRandomQuestionAndShuffle(),
            currentQuestionCount = 0,
            progress = 0f
        )
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

    private fun updateQuestionResult(questionText: String, isCorrect: Boolean) {
        val answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.WRONG
        val questionResult = QuestionResult(
            question = questionText,
            answerState = answerState
        )
        questionResults.add(questionResult)
    }


    fun onSkip() {
        showNextQuestion(usedQuestions.size == MAX_NO_OF_QUESTIONS)
    }


    companion object {
        private const val SCORE_INCREASE = 120
        private const val MAX_NO_OF_QUESTIONS = 5
        private const val INITIAL_SCORE = 0
    }

}