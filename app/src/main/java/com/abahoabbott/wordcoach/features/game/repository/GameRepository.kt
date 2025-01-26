package com.abahoabbott.wordcoach.features.game.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.abahoabbott.wordcoach.features.game.GameViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    suspend fun saveCumulativeScore(score: Int) {
        dataStore.edit { preferences ->
            preferences[SCORE_KEY] = score
        }
    }
    suspend fun getCumulativeScore(): Int {
        return dataStore.data.map { preferences ->
            preferences[SCORE_KEY] ?: 0
        }.first()
    }

    suspend fun resetCumulativeScore() {
        dataStore.edit { preferences ->
            preferences[SCORE_KEY] = 0
        }
    }
    companion object{
         private val SCORE_KEY = intPreferencesKey("cumulative_score")

    }

}