package com.abahoabbott.wordcoach.features.wod

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("word_of_the_day_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val LAST_FETCH_TIME = longPreferencesKey("last_fetch_time")
        private val LAST_WORD = stringPreferencesKey("last_word")
        private val LAST_DEFINITION = stringPreferencesKey("last_definition")
        private val LAST_EXAMPLES = stringPreferencesKey("last_examples")
    }

    val lastFetchTime: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[LAST_FETCH_TIME] ?: 0L }

    val lastWord: Flow<WordOfTheDay?> = context.dataStore.data
        .map { preferences ->
            val word = preferences[LAST_WORD] ?: return@map null
            val definition = preferences[LAST_DEFINITION] ?: ""
            val examplesJson = preferences[LAST_EXAMPLES] ?: return@map WordOfTheDay(
                word,
                definition,
                "",
                emptyList()
            )

            val examples = runCatching {
                JSONObject(examplesJson).getJSONArray("examples")
                    .let { 0.until(it.length()).map { i -> it.getString(i) } }
            }.getOrElse { emptyList() }



            WordOfTheDay(
                word, definition, "",
                examples = examples
            )
        }

    suspend fun saveLastFetchTime(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_FETCH_TIME] = timestamp
        }
    }

    suspend fun saveWordOfTheDay(word: WordOfTheDay) {
        context.dataStore.edit { preferences ->
            preferences[LAST_WORD] = word.word
            preferences[LAST_DEFINITION] = word.definition
            preferences[LAST_EXAMPLES] = JSONObject().put("examples", word.examples).toString()
        }
    }
}
