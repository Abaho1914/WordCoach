package com.abahoabbott.wordcoach.room.wod

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Word of the Day database table.
 * Provides methods for inserting, querying, updating, and deleting WordOfTheDayEntity objects.
 */
@Dao
interface WordsDao {

    /**
     * Inserts a [WordOfTheDayEntity] into the database.
     * If a conflict occurs (e.g., due to primary key), the existing entry will be replaced.
     * Consider using @Upsert if using Room 2.4.0+ for a more explicit insert-or-update behavior.
     *
     * @param wordOfTheDayEntity The entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Or use @Upsert if available and appropriate
    suspend fun upsert(wordOfTheDayEntity: WordOfTheDayEntity)

    /**
     * Updates an existing [WordOfTheDayEntity] in the database.
     * The entity is matched by its primary key.
     *
     * @param wordOfTheDay The entity to update.
     */
    @Update
    suspend fun update(wordOfTheDay: WordOfTheDayEntity)

    /**
     * Retrieves the Word of the Day entity for a specific date.
     *
     * @param date The date string to query for (e.g., "YYYY-MM-DD").
     * @return A Flow emitting the [WordOfTheDayEntity] for the given date, or null if not found.
     */
    @Query("SELECT * FROM word_of_the_day WHERE publishDate = :date")
    fun getWordByDate(date: String): Flow<WordOfTheDayEntity?>

    /**
     * Retrieves the most recently published Word of the Day based on the `publishDate`.
     * Assumes `publishDate` is stored in a format that allows for chronological sorting (e.g., ISO 8601 string or Long timestamp).
     *
     * @return A Flow emitting the most recent [WordOfTheDayEntity], or null if the table is empty.
     */
    @Query("SELECT * FROM word_of_the_day ORDER BY publishDate DESC LIMIT 1")
    fun getMostRecentWord(): Flow<WordOfTheDayEntity?>

    /**
     * Retrieves a Word of the Day entity by its API ID.
     * Useful for checking if a word from the network has been previously stored, regardless of its publish date.
     *
     * @param apiId The unique ID from the external API.
     * @return A Flow emitting the [WordOfTheDayEntity] with the given API ID, or null if not found.
     */
    @Query("SELECT * FROM word_of_the_day WHERE apiId = :apiId")
    fun getWordByApiId(apiId: String): Flow<WordOfTheDayEntity?>

    /**
     * Retrieves all Word of the Day entities ordered by publish date in descending order.
     *
     * @return A Flow emitting a list of all [WordOfTheDayEntity] objects.
     */
    @Query("SELECT * FROM word_of_the_day ORDER BY publishDate DESC")
    fun getAll(): Flow<List<WordOfTheDayEntity>>

    /**
     * Deletes a specific [WordOfTheDayEntity] from the database.
     * The entity is matched by its primary key.
     *
     * @param wordOfTheDay The entity to delete.
     */
    @Delete
    suspend fun delete(wordOfTheDay: WordOfTheDayEntity)

    /**
     * Deletes the Word of the Day entity for a specific date.
     * Useful for replacing today's word during a forced refresh.
     *
     * @param date The date string of the word to delete.
     */
    @Query("DELETE FROM word_of_the_day WHERE publishDate = :date")
    suspend fun deleteWordByDate(date: String)

    /**
     * Deletes all Word of the Day entities from the database.
     */
    @Query("DELETE FROM word_of_the_day")
    suspend fun deleteAll()
}
