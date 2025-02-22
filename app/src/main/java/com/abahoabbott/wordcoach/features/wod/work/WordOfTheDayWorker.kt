package com.abahoabbott.wordcoach.features.wod.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@HiltWorker
class WordOfTheDayWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: WordOfTheDayRepository
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = repository.fetchWordOfDay(forceFetch = true)
            if (result.isSuccess) {
                scheduleNextDailyWork(applicationContext)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "word_of_the_day_worker"
        private const val TIME_ZONE = "Africa/Nairobi"

        fun scheduleInitialWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val delay = calculateInitialDelay()

            val workRequest = OneTimeWorkRequestBuilder<WordOfTheDayWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        private fun calculateInitialDelay(): Long {
            val now = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE))
            val scheduledTime = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE)).apply {
                set(Calendar.HOUR_OF_DAY, 7)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
            }
            return scheduledTime.timeInMillis - now.timeInMillis
        }

        private fun scheduleNextDailyWork(context: Context) {
            val delay = calculateInitialDelay()
            val workRequest = OneTimeWorkRequestBuilder<WordOfTheDayWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}