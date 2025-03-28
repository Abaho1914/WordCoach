package com.abahoabbott.wordcoach.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

//Top-level extension to create DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")


const val LOG_TAG = "WordCoach"

