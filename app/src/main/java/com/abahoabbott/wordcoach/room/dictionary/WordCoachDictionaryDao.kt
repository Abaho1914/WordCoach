package com.abahoabbott.wordcoach.room.dictionary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordCoachDictionaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewWord(newWord: WordCoachDictionary)

    @Query("SELECT * FROM words_dictionary WHERE id = :wordId")
    fun getWordById(wordId: Int): WordCoachDictionary
}
