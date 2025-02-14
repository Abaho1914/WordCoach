package com.abahoabbott.wordcoach.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import com.abahoabbott.wordcoach.common.dataStore
import com.abahoabbott.wordcoach.features.game.repository.GameRepository
import com.abahoabbott.wordcoach.features.wod.DataStoreManager
import com.abahoabbott.wordcoach.features.wod.work.WordOfTheDayRepository
import com.abahoabbott.wordcoach.network.WordnikApiService
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


    /**
     * Provides a [WordOfTheDayRepository] instance.
     *
     * @param wordnikApiService The API service for fetching word data.
     * @param dataStoreManager The manager for caching word-of-the-day data.
     * @return A singleton instance of [WordOfTheDayRepository].
     */
    @Provides
    @Singleton
    fun provideWordOfTheDayRepository(
        wordnikApiService: WordnikApiService,
        dataStoreManager: DataStoreManager
    ): WordOfTheDayRepository {
        return WordOfTheDayRepository(
            dataStoreManager,
            wordnikApiService
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


    /**
     * Provides a [DataStoreManager] instance for managing word-of-the-day data.
     *
     * @param context The application context, provided by Hilt.
     * @return A singleton instance of [DataStoreManager].
     */
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }
}