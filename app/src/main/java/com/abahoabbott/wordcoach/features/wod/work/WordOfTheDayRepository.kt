package com.abahoabbott.wordcoach.features.wod.work

import android.util.Log
import com.abahoabbott.wordcoach.common.DateFormatter
import com.abahoabbott.wordcoach.common.LOG_TAG
import com.abahoabbott.wordcoach.features.wod.WordOfTheDay
import com.abahoabbott.wordcoach.features.wod.toWordOfTheDay
import com.abahoabbott.wordcoach.network.WordnikApiService
import com.abahoabbott.wordcoach.room.WordOfTheDayEntity
import com.abahoabbott.wordcoach.room.WordsDao
import com.abahoabbott.wordcoach.room.toEntity
import kotlinx.coroutines.flow.first


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
     * Fetches the Word of the Day from the local database. If the database is empty
     * or does not contain today's word, a network call is made to fetch a new word.
     *
     * @return [Result] containing the [WordOfTheDay] if successful, or an error if an exception occurs.
     */
    suspend fun fetchWordOfTheDay(): Result<WordOfTheDay> {
        Log.i(LOG_TAG, "Fetching Word of the Day from database")

        return try {
            val today = dateFormatter.getCurrentDate()
            val currentWordEntity = wordsDao.getWordByDate(today).first()

            currentWordEntity?.let {
                Log.i(LOG_TAG, "Current word found: $it")
                return Result.success(it.toDomainModel())
            }

            Log.i(LOG_TAG, "Current word not found in database, making network call")
            fetchFromNetwork()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error fetching Word of the Day: ${e.message}", e)
            Result.failure(e)
        }
    }


    /**
     * Fetches the Word of the Day from the network and persists it to the database.
     *
     * @return [Result] containing the fetched [WordOfTheDay] if successful
     */
    private suspend fun fetchFromNetwork(): Result<WordOfTheDay> {
        return try {
            val wordOfTheDay = wordnikApiService.getWordOfTheDay().toWordOfTheDay()
            val entity = wordOfTheDay.toEntity().apply {
                publishDate = dateFormatter.getCurrentDate()
            }
            wordsDao.insert(entity)
            Log.i(LOG_TAG, "Successfully saved word to database")
            Result.success(wordOfTheDay)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Network call failed: ${e.message}", e)
            Result.failure(e)
        }
    }

}

/**
 * Converts a [WordOfTheDayEntity] to a domain model [WordOfTheDay]
 */
private fun WordOfTheDayEntity.toDomainModel(): WordOfTheDay {
    return WordOfTheDay(
        word = this.word,
        pronunciation = this.pronunciation,
        definition = this.definition,
        examples = this.examples,
        publishDate = this.publishDate,
        note = this.definition.note ?: "Empty note"
    )
}
