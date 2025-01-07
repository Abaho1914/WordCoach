package com.abahoabbott.wordcoach.common

import java.util.Locale

fun modifyExplanationQuestion(sentence: String): String {
    val newSentence = sentence.replace("Which", "").replace("is", "").trimStart()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    return ("$newSentence?")
}

fun scoreBoardMessage(correctAnswers: Int): String {
    return when (correctAnswers) {
        0 -> "Keep playing"
        1 -> "Good Effort"
        2 -> "Great work"
        3 -> "Well done"
        4 -> "Almost perfect"
        5 -> "Perfection"
        else -> "Excellent"
    }

}