package com.abahoabbott.wordcoach.features.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abahoabbott.wordcoach.data.GameUiState
import com.abahoabbott.wordcoach.features.game.repository.GameRepository
import com.abahoabbott.wordcoach.features.results.AnswerState
import com.abahoabbott.wordcoach.features.results.QuestionResult
import com.abahoabbott.wordcoach.nav.NavEvent
import com.abahoabbott.wordcoach.network.WordnikApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) :
    ViewModel() {

    //Game Ui State
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    //set of questions to be used in the game
    private var toBeUsedQuestions: MutableList<WordQuestion> = mutableListOf()

    //set of questions that the user has answered
    private var userAnswers: MutableList<QuestionResult> = mutableListOf()

    //Navigation event
    private val _navigationEvents = Channel<NavEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()


    private var currentDifficulty: Difficulty = Difficulty.EASY // Start with easy difficulty
    private var correctAnswersInLevel: Int = 0
    private var wrongAnswersInLevel: Int = 0

    init {
        viewModelScope.launch {
            resetCumulativeScore()
            val wordOfTheDay = WordnikApi.retrofitService.getWordOfTheDay()
          //  val randomWord = WordnikApi.retrofitService.getRandomWord()
            Log.i("Sunflower:GameViewModel", "Word of the Day: $wordOfTheDay")
          //  Log.i("Sunflower:GameViewModel", "Word of the Day: $randomWord")
        }

        resetGame()
    }


    fun onOptionSelected(selectedOptionId: Int, isCorrect: Boolean) {
        if (toBeUsedQuestions.size <= MAX_NO_OF_QUESTIONS) {
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

        // Update difficulty logic
        if (isCorrect) {
            correctAnswersInLevel++
            if (correctAnswersInLevel >= CORRECT_ANSWERS_TO_LEVEL_UP) {
                levelUp()
            }
        } else {
            wrongAnswersInLevel++
            if (wrongAnswersInLevel >= WRONG_ANSWERS_TO_LEVEL_DOWN) {
                levelDown()
            }
        }
    }


    private fun proceedToNextStep() {
        viewModelScope.launch {
            if (toBeUsedQuestions.size == MAX_NO_OF_QUESTIONS) {
                //navigate to results screen if game is over
                navigateToResultsScreen()
            } else {
                //pick next question if the game is not yet over
                showNextQuestion()
            }

        }
    }

    private suspend fun showNextQuestion() {
        delay(3000L)
        _uiState.update { state ->
            state.copy(
                questionState = state.questionState.copy(
                    currentQuestion = pickNextQuestion(),
                    selectedOptionId = null
                ),
                gameProgress = state.gameProgress.copy(
                    answeredQuestions = userAnswers.size,
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
            attemptedQuestions = userAnswers
        )

        delay(3000L)

        _navigationEvents.send(
            NavEvent.NavigateToResultsScreen(
                gameResult
            )
        )
    }

    private suspend fun getCumulativeScore(): Int {
        return gameRepository.getCumulativeScore()
    }

    private suspend fun saveCumulativeScore(score: Int) {
        gameRepository.saveCumulativeScore(score)
    }

    private suspend fun resetCumulativeScore() = gameRepository.resetCumulativeScore()


    private fun pickNextQuestion(): WordQuestion {
        return gameRepository.getNextQuestion(
            currentDifficulty,
            usedQuestions = toBeUsedQuestions
        )
            .also {
                toBeUsedQuestions.add(it)
            }
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
        toBeUsedQuestions.clear()
        userAnswers.clear()
    }

    private fun updateQuestionResults(isCorrect: Boolean) {
        val answerState = if (isCorrect) AnswerState.CORRECT else AnswerState.WRONG
        val questionResult = QuestionResult(
            question = _uiState.value.questionState.currentQuestion.question,
            answerState = answerState
        )
        userAnswers.add(questionResult)
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
        userAnswers.add(questionResult)
        //
        proceedToNextStep()
    }


    override fun onCleared() {
        viewModelScope.launch {
            resetCumulativeScore()
        }

        super.onCleared()
    }

    private fun levelUp() {
        currentDifficulty = when (currentDifficulty) {
            Difficulty.EASY -> Difficulty.MEDIUM
            Difficulty.MEDIUM -> Difficulty.HARD
            Difficulty.HARD -> Difficulty.HARD // Already at the highest level
        }
        Log.d("GameViewModel", "Level Up: Current difficulty is now $currentDifficulty")
        resetLevelCounters()
    }

    private fun levelDown() {
        currentDifficulty = when (currentDifficulty) {
            Difficulty.EASY -> Difficulty.EASY // Already at the lowest level
            Difficulty.MEDIUM -> Difficulty.EASY
            Difficulty.HARD -> Difficulty.MEDIUM
        }
        Log.d("GameViewModel", "Level Down: Current difficulty is now $currentDifficulty")
        resetLevelCounters()
    }

    private fun resetLevelCounters() {
        correctAnswersInLevel = 0
        wrongAnswersInLevel = 0
    }

    companion object {
        //Score increase when user answers correctly
        private const val SCORE_INCREASE = 120

        //Max number of questions are user can attempt per game
        private const val MAX_NO_OF_QUESTIONS = 5

        private const val CORRECT_ANSWERS_TO_LEVEL_UP = 3
        private const val WRONG_ANSWERS_TO_LEVEL_DOWN = 2
    }

}
