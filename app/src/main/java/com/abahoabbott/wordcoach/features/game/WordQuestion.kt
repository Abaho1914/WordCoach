package com.abahoabbott.wordcoach.features.game


import kotlinx.serialization.Serializable

@Serializable
data class WordQuestion(
    val questionId: Int = 0,
    val question: String = "",
    val options: GameOptions = listOf(),
    val difficulty: Difficulty = Difficulty.EASY // Add difficulty level
)

typealias GameOptions = List<GameOption>

@Serializable
data class GameOption(
    val optionId: Int = 0,
    val text: String = "",
    val isCorrect: Boolean = false
)

enum class Difficulty {
    EASY, MEDIUM, HARD
}

// Helper function to create questions
fun createWordQuestion(
    id: Int,
    question: String,
    options: List<Pair<String, Boolean>>,
    difficulty: Difficulty
): WordQuestion {
    val gameOptions = options.mapIndexed { index, option ->
        GameOption(optionId = index, text = option.first, isCorrect = option.second)
    }
    return WordQuestion(questionId = id, question = question, options = gameOptions, difficulty = difficulty)
}

// Initial questions
val initialQuestions = listOf(
    createWordQuestion(
        id = 0,
        question = "Which word is similar to happy?",
        options = listOf("Joyful" to true, "Sad" to false),
        difficulty = Difficulty.EASY
    ),
    createWordQuestion(
        id = 1,
        question = "Which word is opposite of start?",
        options = listOf("Begin" to false, "End" to true),
        difficulty = Difficulty.EASY
    ),
    createWordQuestion(
        id = 2,
        question = "Which word is similar to big?",
        options = listOf("Large" to true, "Small" to false),
        difficulty = Difficulty.EASY
    ),
    createWordQuestion(
        id = 3,
        question = "Which word is opposite of hot?",
        options = listOf("Cold" to true, "Warm" to false),
        difficulty = Difficulty.EASY
    )
)

// Additional questions with varying difficulty levels
val moreQuestions = listOf(
    createWordQuestion(
        id = 4,
        question = "Which word is opposite of empty?",
        options = listOf("Full" to true, "Vacant" to false),
        difficulty = Difficulty.MEDIUM
    ),
    createWordQuestion(
        id = 5,
        question = "Which word is similar to difficult?",
        options = listOf("Hard" to true, "Easy" to false),
        difficulty = Difficulty.MEDIUM
    ),
    createWordQuestion(
        id = 6,
        question = "Which word is opposite of clean?",
        options = listOf("Dirty" to true, "Tidy" to false),
        difficulty = Difficulty.HARD
    ),
    createWordQuestion(
        id = 7,
        question = "Which word is the synonym of 'abundant'?",
        options = listOf("Plentiful" to true, "Scarce" to false),
        difficulty = Difficulty.HARD
    )
)

// Combine all questions
val allQuestions = initialQuestions + moreQuestions
