package com.abahoabbott.wordcoach.nav

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.abahoabbott.wordcoach.BuildConfig
import com.abahoabbott.wordcoach.common.LOG_TAG
import com.abahoabbott.wordcoach.features.wod.work.WordOfTheDayWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class WordCoachHiltApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        scheduleDailyWordFetch()
    }

    /**
     * Schedules the single periodic worker responsible for fetching the word of the day.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleDailyWordFetch() {
        Timber.tag(LOG_TAG).i("WordCoachHiltApp: Scheduling daily word fetch work.")
        // Call the simplified scheduling function in the Worker's companion object
        WordOfTheDayWorker.scheduleDailyWordFetchWork(this)
    }

}