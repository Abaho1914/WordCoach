package com.abahoabbott.wordcoach.room.dictionary

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abahoabbott.wordcoach.room.wod.Converters

@Database(entities = [WordCoachDictionary::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordCoachDictionaryDatabase : RoomDatabase() {
    abstract fun wordCoachDictionaryDao(): WordCoachDictionaryDao

    companion object {

        const val DATABASE_NAME = "words_dictionary_database"

    }
}