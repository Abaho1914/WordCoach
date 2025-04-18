package com.abahoabbott.wordcoach.features.wod.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * WordOfTheDayWorker handles the scheduled fetching of a new word each day.
 *
 * This worker is designed to run daily at a specific time (7:00 AM EAT) to fetch
 * the new Word of the Day. It utilizes both a precise one-time scheduling approach
 * and a backup periodic approach to ensure reliability.
 *
 * Features:
 * - Scheduled to run at 7:00 AM East Africa Time
 * - Requires network connectivity and non-low battery
 * - Implements retry logic with linear backoff
 * - Provides backup periodic scheduling for reliability
 *
 * @property repository The repository responsible for fetching the word data
 */
@HiltWorker
class WordOfTheDayWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: WordOfTheDayRepository
) : CoroutineWorker(ctx, params) {

    /**
     * Executes the worker's task to fetch the word of the day.
     *
     * This method runs on a background thread and attempts to fetch the
     * latest word of the day from the repository. It handles errors and
     * determines whether to report success or schedule a retry.
     *
     * @return Result indicating success, failure, or retry
     */
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("WordOfTheDayWorker: Starting work")
                val result = repository.forceRefreshTodayWord()
                if (result.isSuccess) {
                    Timber.d("WordOfTheDayWorker: Successfully fetched word of the day")
                    // Schedule the next day's fetch after successful completion
                    scheduleNextDailyWork(applicationContext)
                    Result.success()
                } else {
                    Timber.w("WordOfTheDayWorker: Failed to fetch word, will retry")
                    // Optionally capture the specific error for analysis
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    Timber.w("Error details: $errorMessage")
                    Result.retry()
                }
            } catch (e: Exception) {
                Timber.e(e, "WordOfTheDayWorker: Exception occurred")
                Result.retry()
            }
        }
    }

    companion object {
        /** Unique identifier for the main daily work request */
        private const val WORK_NAME = "word_of_the_day_worker"

        /** Unique identifier for the backup periodic work request */
        private const val BACKUP_WORK_NAME = "word_of_the_day_backup_worker"

        /** Time zone used for scheduling (East Africa Time) */
        private const val TIME_ZONE = "Africa/Nairobi"

        /** Hour of day (in 24-hour format) when the word should be fetched */
        private const val FETCH_HOUR = 7 // 7:00 AM

        /** Delay between retry attempts in minutes */
        private const val BACKOFF_DELAY_MINUTES = 15L

        /**
         * Initializes both the precise daily work and a backup periodic work.
         *
         * This should be called during app startup, typically in the Application class
         * or a startup service.
         *
         * @param context Application context used to access WorkManager
         */
        fun scheduleInitialWork(context: Context) {
            scheduleNextDailyWork(context)
            scheduleBackupPeriodicWork(context)
        }

        /**
         * Schedules a one-time work request for the next day at the specified time.
         *
         * This method calculates the time until the next scheduled run (7:00 AM EAT)
         * and creates a precisely-timed work request.
         *
         * @param context Application context used to access WorkManager
         */
        fun scheduleNextDailyWork(context: Context) {
            val constraints = createWorkConstraints()
            val delay = calculateInitialDelay()

            val workRequest = OneTimeWorkRequestBuilder<WordOfTheDayWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.LINEAR,
                    BACKOFF_DELAY_MINUTES,
                    TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

            Timber.d("Scheduled next word fetch in ${delay/1000/60} minutes")
        }

        /**
         * Schedules a backup periodic work that runs daily to ensure updates aren't missed.
         *
         * This serves as a failsafe in case the one-time work fails to execute or
         * fails to schedule its successor. The flexible execution window allows
         * the system to optimize battery usage by batching operations.
         *
         * @param context Application context used to access WorkManager
         */
        private fun scheduleBackupPeriodicWork(context: Context) {
            val constraints = createWorkConstraints()

            val periodicRequest = PeriodicWorkRequestBuilder<WordOfTheDayWorker>(
                24, TimeUnit.HOURS, // Repeat every 24 hours
                2, TimeUnit.HOURS   // With flexibility window of 2 hours
            )
                .setConstraints(constraints)
                .addTag(BACKUP_WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                BACKUP_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Don't replace if already scheduled
                periodicRequest
            )

            Timber.d("Scheduled backup daily word fetch")
        }

        /**
         * Creates common work constraints for both scheduling methods.
         *
         * Current constraints require:
         * - Network connectivity
         * - Battery not in low state
         *
         * @return Constraints for the work requests
         */
        private fun createWorkConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        }

        /**
         * Calculates the delay in milliseconds until the next scheduled run time.
         *
         * Calculates time until 7:00 AM East Africa Time. If current time is after
         * today's scheduled time, it calculates for tomorrow.
         *
         * @return Delay in milliseconds until next scheduled time
         */
        private fun calculateInitialDelay(): Long {
            val now = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE))
            val scheduledTime = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE)).apply {
                set(Calendar.HOUR_OF_DAY, FETCH_HOUR)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // If current time is after scheduled time, schedule for tomorrow
                if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
            }
            return scheduledTime.timeInMillis - now.timeInMillis
        }
    }
}