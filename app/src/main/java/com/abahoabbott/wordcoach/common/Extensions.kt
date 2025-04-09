package com.abahoabbott.wordcoach.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Modifies a question sentence to make it more suitable for explanations.
 *
 * This function:
 * 1. Removes the leading "Which" word
 * 2. Removes the "is" verb
 * 3. Trims any leading whitespace
 * 4. Capitalizes the first letter of the resulting sentence
 *
 * @param sentence The original question sentence to modify (e.g., "Which word is similar to happy?")
 * @return The modified sentence (e.g., "Word similar to happy?")
 *
 * @throws IllegalArgumentException if the input sentence is blank after processing
 */
fun modifyExplanationQuestion(sentence: String): String {
    require(sentence.isNotBlank()) { "Input sentence cannot be blank" }

    return sentence
        .removePrefix("Which")
        .removePrefix("which") // Case-insensitive handling
        .replace(Regex("\\bis\\b"), "") // Remove standalone "is" (not parts of other words)
        .trim()
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        .also { result ->
            require(result.isNotBlank()) {
                "Processing resulted in blank string. Check input format."
            }
        }
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

