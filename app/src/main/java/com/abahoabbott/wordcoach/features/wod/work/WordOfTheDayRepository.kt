package com.abahoabbott.wordcoach.features.wod.work

import android.util.Log
import com.abahoabbott.wordcoach.common.DateFormatter
import com.abahoabbott.wordcoach.common.LOG_TAG
import com.abahoabbott.wordcoach.features.wod.WordOfTheDay
import com.abahoabbott.wordcoach.features.wod.toWordOfTheDay
import com.abahoabbott.wordcoach.network.WordnikApiService
import com.abahoabbott.wordcoach.room.wod.WordsDao
import com.abahoabbott.wordcoach.room.wod.toDomainModel
import com.abahoabbott.wordcoach.room.wod.toEntity
import kotlinx.coroutines.flow.firstOrNull


/**
 * Repository for managing Word of the Day operations, including local database access
 * and remote network calls.
 *
 * @property wordnikApiService API service for fetching word data
 * @property wordsDao Data Access Object for word database operations
 * @property dateFormatter Utility for date formatting operations
 */
class WordOfTheDayRepository(
    private val wordnikApiService: WordnikApiService,
    private val wordsDao: WordsDao,
    private val dateFormatter: DateFormatter
) {

    /**
     * Explicitly fetches a fresh word from the network API.
     * Should only be called when needed (e.g., manual refresh or missing word).
     */
    private suspend fun fetchWordFromNetwork(): Result<WordOfTheDay> {
        return try {
            val wordOfTheDay = wordnikApiService.getWordOfTheDay().toWordOfTheDay()

            //Double check that we don't already have this word
            val existingWord = wordsDao.getWordByApiId(wordOfTheDay.apiId).firstOrNull()

            if (existingWord != null) {
                Log.i(LOG_TAG, "Word already exists in database")
                Result.success(existingWord.toDomainModel())
            } else {
                // Insert the new word
                wordsDao.insert(wordOfTheDay.toEntity())
                Log.i(LOG_TAG, "New word fetched and saved to database")
                Result.success(wordOfTheDay)
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error fetching word from network", e)
            Result.failure(e)
        }
    }

    /**
     * Forces a refresh of today's word from the network.
     * This is used for explicit refresh actions by the user.
     */
    suspend fun forceRefreshTodayWord(): Result<WordOfTheDay> {
        return fetchWordFromNetwork()
    }

    suspend fun getLatestWordFromDatabase(): Result<WordOfTheDay>{
        return try {
            val currentWord = wordsDao.getMostRecentWord().firstOrNull()
            if (currentWord != null){
                Result.success(currentWord.toDomainModel())
            } else{
                Result.failure(Exception("There is no word in database"))
            }

        }
        catch (e: Exception){
            Result.failure(e)
        }

    }
}