package com.abahoabbott.wordcoach.room.wod

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WordOfTheDayEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsDao(): WordsDao

    companion object {

        const val DATABASE_NAME = "words_database"

    }
}