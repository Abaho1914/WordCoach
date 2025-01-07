package com.abahoabbott.wordcoach.features.game

import kotlinx.serialization.Serializable

@Serializable
data class WordQuestion(
    val questionId: Int = 0,
    val question: String = "",
    val options: GameOptions = listOf()
)


typealias GameOptions = List<GameOption>

@Serializable
data class GameOption(
    val optionId: Int = 0,
    val text: String = "",
    val isCorrect: Boolean = false
)


val initialOptions = listOf(
    WordQuestion(
        questionId = 0,
        question = "Which word is similar to flow",
        options = listOf(GameOption(0, "Movement", true), GameOption(1, "Judgement", false))
    ),
    WordQuestion(
        questionId = 1,
        question = "Which word is similar to please",
        options = listOf(GameOption(0, "Mostly", false), GameOption(1, "Kindly", true))
    ),
    WordQuestion(
        questionId = 2,
        question = "Which word is similar to beautiful",
        options = listOf(GameOption(0, "Negative", false), GameOption(1, "Attractive", true))
    ),
    WordQuestion(
        questionId = 3,
        question = "Which word is similar to thick",
        options = listOf(GameOption(0, "Outside", false), GameOption(1, "Wide", true))
    ),
    WordQuestion(
        questionId = 4,
        question = "Which word is similar to palatable",
        options = listOf(GameOption(0, "Convertible", false), GameOption(1, "Edible", true))
    ),
)

val moreQuestions = listOf(
    WordQuestion(
        questionId = 5,
        question = "Which word is similar to happy",
        options = listOf(GameOption(0, "Joyful", true), GameOption(1, "Sad", false))
    ),
    WordQuestion(
        questionId = 6,
        question = "Which word is opposite of start",
        options = listOf(GameOption(0, "Begin", false), GameOption(1, "End", true))
    ),
    WordQuestion(
        questionId = 7,
        question = "Which word is similar to big",
        options = listOf(GameOption(0, "Large", true), GameOption(1, "Small", false))
    ),
    WordQuestion(
        questionId = 8,
        question = "Which word is opposite of hot",
        options = listOf(GameOption(0, "Cold", true), GameOption(1, "Warm", false))
    ),
    WordQuestion(
        questionId = 9,
        question = "Which word is similar to quick",
        options = listOf(GameOption(0, "Fast", true), GameOption(1, "Slow", false))
    ),
    WordQuestion(
        questionId = 10,
        question = "Which word is opposite of empty",
        options = listOf(GameOption(0, "Full", true), GameOption(1, "Vacant", false))
    ),
    WordQuestion(
        questionId = 11,
        question = "Which word is similar to difficult",
        options = listOf(GameOption(0, "Hard", true), GameOption(1, "Easy", false))
    ),
    WordQuestion(
        questionId = 12,
        question = "Which word is opposite of clean",
        options = listOf(GameOption(0, "Dirty", true), GameOption(1, "Tidy", false))
    )
)
val allQuestions = initialOptions + moreQuestions

