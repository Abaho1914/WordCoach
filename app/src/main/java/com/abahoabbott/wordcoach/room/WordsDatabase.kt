package com.abahoabbott.wordcoach.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WordOfTheDayEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsDao(): WordsDao

    companion object {
        @Volatile
        private var Instance: WordsDatabase? = null
        fun getWordsDatabase(context: Context) : WordsDatabase{
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WordsDatabase::class.java, "words_database")
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}