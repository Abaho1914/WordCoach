package com.abahoabbott.wordcoach.nav

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.abahoabbott.wordcoach.common.LOG_TAG
import com.abahoabbott.wordcoach.features.wod.work.WordOfTheDayWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WordCoachHiltApp : Application() {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    // Defer WorkManager configuration to onCreate
    private val workManagerConfig by lazy {
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize WorkManager after injection
        initializeWorkManager()
        scheduleDailyWordFetch()
    }

    private fun initializeWorkManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WorkManager.initialize(this, workManagerConfig)
        }
    }

    private fun scheduleDailyWordFetch() {
        Log.i(LOG_TAG, "WordCoachHiltApp:Scheduling daily word fetch")
        WordOfTheDayWorker.scheduleInitialWork(this)
    }
}