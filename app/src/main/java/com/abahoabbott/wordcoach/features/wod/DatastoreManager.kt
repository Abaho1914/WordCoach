package com.abahoabbott.wordcoach.features.wod

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


// Extension property for DataStore instance
private val Context.dataStore by preferencesDataStore("word_of_the_day_prefs")

/**
 * Manages saving and retrieving Word of the Day data using Android DataStore.
 *
 * @param context The Android Context used to access the DataStore.
 */
class DataStoreManager(private val context: Context) {

    /**
     * Encapsulated preference keys used in the DataStore.
     */
    private object PreferencesKeys {
        val LAST_FETCH_TIME = longPreferencesKey("last_fetch_time")
        // Store the entire WordOfTheDay as a JSON string.
        val WORD_OF_THE_DAY = stringPreferencesKey("word_of_the_day")
    }

    // Json serializer instance with configuration
   private val json = Json { ignoreUnknownKeys = true }


    /**
     * A [Flow] that emits the timestamp of the last fetch operation.
     */
    val lastFetchTime: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.LAST_FETCH_TIME] ?: 0L }


    /**
     * A [Flow] that emits the last stored [WordOfTheDay] object.
     *
     * Returns null if no word is stored.
     */

    val lastWord: Flow<WordOfTheDay?> = context.dataStore.data
        .map { preferences ->
            // Retrieve the stored JSON string.
            val wordJson = preferences[PreferencesKeys.WORD_OF_THE_DAY] ?: return@map null
            // Deserialize JSON to WordOfTheDay object.
            runCatching {
                json.decodeFromString<WordOfTheDay>(wordJson)
            }.getOrElse { null }
        }

    /**
     * Saves the timestamp of the last fetch operation.
     *
     * @param timestamp The timestamp to be saved.
     */
    suspend fun saveLastFetchTime(timestamp: Long) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.LAST_FETCH_TIME] = timestamp
            }
        }
    }


    /**
     * Saves the given [WordOfTheDay] object into the DataStore.
     *
     * @param word The [WordOfTheDay] object to be saved.
     */
        suspend fun saveWordOfTheDay(word: WordOfTheDay) {
            withContext(Dispatchers.IO) {
                context.dataStore.edit { preferences ->
                    // Serialize the WordOfTheDay object to a JSON string.
                    preferences[PreferencesKeys.WORD_OF_THE_DAY] = json.encodeToString(word)
                }
            }
        }
}
