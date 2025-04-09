package com.abahoabbott.wordcoach.features.results

import com.abahoabbott.wordcoach.features.game.Difficulty
import kotlinx.serialization.Serializable

data class Explanation(
    val questionId: Int = 0,
    val question: String = "",
    val options: List<WordCoachWord> = listOf(),
    val difficulty: Difficulty = Difficulty.EASY,
)


@Serializable
data class WordCoachWord(
    val id: Int,
    val word: String,
    val definition: String,
    val example: String

)

object WordCoachExamples {
    val wordList = listOf(
        WordCoachWord(
            1,
            "happy",
            "feeling or showing pleasure or contentment",
            "Melissa came in looking happy and excited."
        ),
        WordCoachWord(
            2,
            "cheerful",
            "noticeably happy and optimistic",
            "How can she be so cheerful at five o'clock in the morning?"
        ),
        WordCoachWord(
            3,
            "sad",
            "feeling or showing sorrow",
            "He felt sad after hearing the bad news."
        ),
        WordCoachWord(
            4,
            "angry",
            "feeling or showing strong annoyance",
            "She was angry when her brother broke her toy."
        ),
        WordCoachWord(
            5,
            "brave",
            "ready to face danger or pain",
            "The brave firefighter rescued the child."
        ),
        WordCoachWord(
            6,
            "kind",
            "friendly and generous",
            "She was kind enough to help the lost tourist."
        ),
        WordCoachWord(
            7,
            "honest",
            "telling the truth",
            "An honest person always admits their mistakes."
        ),
        WordCoachWord(
            8,
            "smart",
            "intelligent or clever",
            "The smart student solved the math problem quickly."
        ),
        WordCoachWord(
            9,
            "funny",
            "causing laughter or amusement",
            "The comedian told a funny joke."
        ),
        WordCoachWord(
            10,
            "strong",
            "having physical power",
            "He is strong enough to lift heavy boxes."
        ),
        WordCoachWord(11, "weak", "lacking physical strength", "After being sick, she felt weak."),
        WordCoachWord(12, "fast", "moving quickly", "The fast runner won the race."),
        WordCoachWord(
            13,
            "slow",
            "moving at a low speed",
            "The slow turtle took hours to cross the road."
        ),
        WordCoachWord(14, "bright", "giving out much light", "The bright sun made the room warm."),
        WordCoachWord(
            15,
            "dark",
            "with little or no light",
            "The room was dark after the lights went out."
        ),
        WordCoachWord(16, "loud", "producing much noise", "The loud music kept me awake."),
        WordCoachWord(
            17,
            "quiet",
            "making little or no noise",
            "The library was so quiet you could hear a pin drop."
        ),
        WordCoachWord(18, "easy", "not difficult", "The test was easy for her."),
        WordCoachWord(19, "hard", "requiring a lot of effort", "The puzzle was hard to solve."),
        WordCoachWord(20, "clean", "free from dirt", "She keeps her room very clean.")
    )

}