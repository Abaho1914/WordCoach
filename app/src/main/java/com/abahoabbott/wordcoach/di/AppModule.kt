package com.abahoabbott.wordcoach.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import com.abahoabbott.wordcoach.common.DateFormatter
import com.abahoabbott.wordcoach.common.SimpleDateFormatter
import com.abahoabbott.wordcoach.common.dataStore
import com.abahoabbott.wordcoach.features.game.repository.GameRepository
import com.abahoabbott.wordcoach.features.wod.work.WordOfTheDayRepository
import com.abahoabbott.wordcoach.network.WordnikApiService
import com.abahoabbott.wordcoach.room.wod.WordsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides application-wide dependencies.
 *
 * This module is installed in the [SingletonComponent], meaning all dependencies provided here
 * will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides a [DataStore] instance for managing preferences.
     *
     * @param context The application context, provided by Hilt.
     * @return A singleton instance of [DataStore<Preferences>].
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }


    /**
     * Provides a [GameRepository] instance.
     *
     * @param dataStore The [DataStore] instance for managing game-related preferences.
     * @return A singleton instance of [GameRepository].
     */
    @Provides
    @Singleton
    fun provideGameRepository(dataStore: DataStore<Preferences>): GameRepository {
        return GameRepository(dataStore)
    }

    @Provides
    fun provideDateFormatter(): DateFormatter = SimpleDateFormatter()


    /**
     * Provides a [WordOfTheDayRepository] instance.
     *
     * This function constructs and returns a singleton instance of the [WordOfTheDayRepository].
     * It depends on [WordnikApiService], [WordsDao], and [DateFormatter] to function.
     * These dependencies are also provided by Dagger/Hilt.
     *
     * @param wordnikApiService The API service for fetching word data.
     * @param wordsDao The Data Access Object for interacting with the words database.
     * @param dateFormatter The utility for formatting dates.
     * @return A singleton instance of [WordOfTheDayRepository].
     */
    @Provides
    @Singleton
    fun provideWordOfTheDayRepository(
        wordnikApiService: WordnikApiService,
        wordsDao: WordsDao,
        dateFormatter: DateFormatter
    ): WordOfTheDayRepository {
        return WordOfTheDayRepository(
            wordnikApiService,
            wordsDao,
            dateFormatter
        )
    }


    /**
     * Provides a [WorkManager] instance for managing background tasks.
     *
     * @param context The application context, provided by Hilt.
     * @return A singleton instance of [WorkManager].
     */
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

}