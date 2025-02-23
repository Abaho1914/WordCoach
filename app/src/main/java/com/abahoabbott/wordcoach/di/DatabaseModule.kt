package com.abahoabbott.wordcoach.di

import android.content.Context
import androidx.room.Room
import com.abahoabbott.wordcoach.room.WordsDao
import com.abahoabbott.wordcoach.room.WordsDatabase
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
    fun provideWordsDao(database: WordsDatabase): WordsDao {
        return database.wordsDao()
    }
}