package com.abahoabbott.wordcoach.features.game

class GameManager {
    var currentDifficulty = Difficulty.EASY
    var correctAnswersCount = 0
    var incorrectAnswersCount = 0
    val maxQuestionsPerLevel = 5 // Number of questions before changing difficulty


    fun adjustDifficulty() {
        when {
            correctAnswersCount >= maxQuestionsPerLevel -> {
                // Promote to the next level
                currentDifficulty = when (currentDifficulty) {
                    Difficulty.EASY -> Difficulty.MEDIUM
                    Difficulty.MEDIUM -> Difficulty.HARD
                    Difficulty.HARD -> Difficulty.HARD // Stay at the hardest level
                }
                correctAnswersCount = 0
                incorrectAnswersCount = 0
            }
            incorrectAnswersCount >= maxQuestionsPerLevel -> {
                // Demote to the previous level
                currentDifficulty = when (currentDifficulty) {
                    Difficulty.HARD -> Difficulty.MEDIUM
                    Difficulty.MEDIUM -> Difficulty.EASY
                    Difficulty.EASY -> Difficulty.EASY // Stay at the easiest level
                }
                correctAnswersCount = 0
                incorrectAnswersCount = 0
            }
        }
    }

    fun getQuestionsByDifficulty(difficulty: Difficulty): List<WordQuestion> {
        return allQuestions.filter { it.difficulty == difficulty }
    }

}