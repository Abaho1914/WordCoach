package com.abahoabbott.wordcoach.features.game.new


enum class Category {
    OPPOSITE,
    SIMILAR
}


data class WordCoachQuestion(
    val questionId: Int = 0,
    val questionWord: Option = GameManager.shine,
    val category: Category = Category.SIMILAR,
    val correctOptionId: Int = 21,
    val options: Pair<Option, Option> = Pair(GameManager.beam, GameManager.slam)
)

data class Option(
    val optionId: Int,
    val word: String,
    val definition: String,
    val example: String
)

object GameManager {

    val shine = Option(
        23,
        "shine",
        "(Of the sun or another source of light) give out a bright light.",
        "the sun shone through the window"
    )

    val beam = Option(
        21,
        "beam",
        "The word beam means(of the sun or another source of light) shine brightly",
        "the sun's rays beamed down"
    )

    val slam = Option(
        22,
        "slam",
        "The word slam means shut (a door, window, or lid) forcefully and loudly.",
        "he slams the door behind him as he leaves"
    )

    val options = listOf(
        Option(
            1,
            "happy",
            "feeling or showing pleasure or contentment",
            "Melissa came in looking happy and excited."
        ),
        Option(
            2,
            "cheerful",
            "noticeably happy and optimistic",
            "How can she be so cheerful at five o'clock in the morning?"
        ),
        Option(
            3,
            "sad",
            "feeling or showing sorrow",
            "He felt sad after hearing the bad news."
        ),
        Option(
            4,
            "angry",
            "feeling or showing strong annoyance",
            "She was angry when her brother broke her toy."
        ),
        Option(
            5,
            "brave",
            "ready to face danger or pain",
            "The brave firefighter rescued the child."
        )
    )
    val listOfQuestions = listOf(
        WordCoachQuestion(
            1,
            options.random(),
            Category.OPPOSITE,
            1,
            Pair(options[0], options[1])
        ),
        WordCoachQuestion(
            2,
            options.random(),
            Category.SIMILAR,
            2,
            Pair(options[0], options[1])
        ),
        WordCoachQuestion(
            3,
            options.random(),
            Category.OPPOSITE,
            1,
            Pair(options[0], options[1])
        ),
        WordCoachQuestion(
            4,
            options.random(),
            Category.SIMILAR,
            1,
            Pair(options.random(), options.random())
        ),
        WordCoachQuestion(
            5,
            options.random(),
            Category.OPPOSITE,
            1,
            Pair(options.random(), options.random())
        ),
    )


    fun findQuestionById(questionId: Int): WordCoachQuestion {
        return listOfQuestions.find {
            it.questionId == questionId
        } ?: WordCoachQuestion()
    }

    fun generateQuestion(question: WordCoachQuestion): String {
        val category = if (question.category == Category.SIMILAR) "similar to" else "opposite of"
        return "What word is $category ${question.questionWord.word} "
    }

}