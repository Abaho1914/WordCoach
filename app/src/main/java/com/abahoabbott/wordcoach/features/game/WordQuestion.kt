package com.abahoabbott.wordcoach.features.game


import com.abahoabbott.wordcoach.features.results.WordCoachWord
import kotlinx.serialization.Serializable

@Serializable
data class WordQuestion(
    val questionId: Int = 0,
    val question: String = "",
    val options: GameOptions = listOf(),
    val difficulty: Difficulty = Difficulty.EASY
)

typealias GameOptions = List<GameOption>

@Serializable
data class GameOption(
    val optionId: Int = 0,
    val text: WordCoachWord = WordCoachWord(0, "", "", ""),
    val isCorrect: Boolean = false
)

/**
 * Enum for game difficulty levels.
 */
enum class Difficulty {
    EASY, MEDIUM, HARD
}

val sampleQn = WordQuestion(
    questionId = 1,
    question = "What word is similar to shine?",
    options = listOf(
        GameOption(
            0,
            WordCoachWord(
                21,
                "beam",
                "The word beam means(of the sun or another source of light) shine brightly",
                "the sun's rays beamed down"
            ),
            true
        ),
        GameOption(
            1,
            WordCoachWord(
                22,
                "slam",
                "The word slam means shut (a door, window, or lid) forcefully and loudly.",
                "he slams the door behind him as he leaves"
            ),
            false
        )
    ),
    difficulty = Difficulty.EASY

)



