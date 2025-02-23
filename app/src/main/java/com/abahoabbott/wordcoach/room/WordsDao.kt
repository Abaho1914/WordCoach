package com.abahoabbott.wordcoach.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wordOfTheDayEntity: WordOfTheDayEntity)

    @Update
    suspend fun update(wordOfTheDay: WordOfTheDayEntity)


    @Query("SELECT * FROM word_of_the_day WHERE publishDate = :date")
    fun getWordByDate(date: String): Flow<WordOfTheDayEntity?>
    @Query("SELECT * FROM word_of_the_day ORDER BY publishDate DESC")
    fun getAll(): Flow<List<WordOfTheDayEntity>>


    @Delete
    suspend fun delete(wordOfTheDay: WordOfTheDayEntity)

    @Query("DELETE FROM word_of_the_day")
    suspend fun deleteAll()
}