package com.abahoabbott.wordcoach.features.wod

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.dataStore by preferencesDataStore("word_of_the_day_prefs")

/**
 * Manages persistent storage for Word of the Day data using Android Preferences DataStore.
 *
 * This class handles:
 * - Storing and retrieving the last fetch timestamp
 * - Serializing/deserializing WordOfTheDay objects to/from JSON
 * - Providing reactive streams of stored data through Flow
 * - Atomic operations for data consistency
 *
 * @property context Android context used to access the DataStore
 */
class DataStoreManager(private val context: Context) {

    /**
     * Preference keys used for DataStore operations.
     */
    private object PreferencesKeys {
        val LAST_FETCH_TIME = longPreferencesKey("last_fetch_time")
        val WORD_OF_THE_DAY = stringPreferencesKey("word_of_the_day")
    }

    /**
     * JSON serializer configuration with:
     * - Ignore unknown JSON properties
     * - Coerce invalid values to defaults
     */
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Flow emitting the timestamp of the last successful word fetch.
     *
     * Emits:
     * - 0L if no timestamp is stored
     * - Latest timestamp as Long for successful fetches
     *
     * Automatically recovers from read errors by emitting empty preferences.
     */
    val lastFetchTime: Flow<Long> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.LAST_FETCH_TIME] ?: 0L }

    /**
     * Flow emitting the cached WordOfTheDay object if available.
     *
     * Emits:
     * - null if no word is stored or if deserialization fails
     * - Latest valid WordOfTheDay object when available
     *
     * Handles:/
     * - JSON deserialization errors
     * - DataStore read errors (emits null)
     */
    val lastWord: Flow<WordOfTheDay?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[PreferencesKeys.WORD_OF_THE_DAY]?.let { jsonString ->
                try {
                    json.decodeFromString<WordOfTheDay>(jsonString)
                } catch (e: SerializationException) {
                    null
                }
            }
        }

    /**
     * Atomically saves both the word data and its associated timestamp.
     *
     * @param word The WordOfTheDay object to store
     * @param timestamp The epoch millis timestamp of when the word was fetched
     * @throws DataStoreOperationException if writing to DataStore fails
     */
    suspend fun saveWordData(word: WordOfTheDay, timestamp: Long) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.LAST_FETCH_TIME] = timestamp
                preferences[PreferencesKeys.WORD_OF_THE_DAY] = json.encodeToString(word)
            }
        } catch (e: IOException) {
            throw DataStoreOperationException("Failed to save word data", e)
        }
    }

    /**
     * Clears all stored word data and timestamps.
     *
     * Primarily used for:
     * - Debugging purposes
     * - Resetting user data
     * - Handling logout scenarios
     */
    suspend fun clearData() {
        context.dataStore.edit {
            it.remove(PreferencesKeys.LAST_FETCH_TIME)
            it.remove(PreferencesKeys.WORD_OF_THE_DAY)
        }
    }
}

/**
 * Exception thrown when DataStore operations fail.
 *
 * @property message Human-readable error description
 * @property cause Root exception that triggered the failure
 */
class DataStoreOperationException(message: String, cause: Throwable) : Exception(message, cause)