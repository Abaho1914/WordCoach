package com.abahoabbott.wordcoach.nav

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WordCoachHiltApp() : Application(), Configuration.Provider {

  //  @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
           // .setWorkerFactory(workerFactory)
            .build()

}
