package com.abahoabbott.wordcoach.di

import android.content.Context
import androidx.room.Room
import com.abahoabbott.wordcoach.room.dictionary.WordCoachDictionaryDao
import com.abahoabbott.wordcoach.room.dictionary.WordCoachDictionaryDatabase
import com.abahoabbott.wordcoach.room.wod.WordsDao
import com.abahoabbott.wordcoach.room.wod.WordsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWordDatabase(
        @ApplicationContext context: Context
    ): WordsDatabase {
        return Room.databaseBuilder(
            context,
            WordsDatabase::class.java,
            WordsDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Add proper migrations in production
            .build()
    }

    @Provides
    @Singleton
    fun provideWordCoachDictionaryDatabase(
        @ApplicationContext context: Context
    ): WordCoachDictionaryDatabase {
        return Room.databaseBuilder(
            context,
            WordCoachDictionaryDatabase::class.java,
            WordCoachDictionaryDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Add proper migrations in production
            .build()
    }




    @Provides
    @Singleton
    fun provideWordsDao(database: WordsDatabase): WordsDao {
        return database.wordsDao()
    }

    @Provides
    @Singleton
    fun providesWordCoachDictionaryDao(database: WordCoachDictionaryDatabase): WordCoachDictionaryDao{
        return database.wordCoachDictionaryDao()
    }
}