package com.abahoabbott.wordcoach.features.wod.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy // Correct import
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * WordOfTheDayWorker handles the scheduled fetching of a new word each day.
 *
 * This worker is designed to run periodically, aiming for approximately 7:00 AM EAT daily,
 * using WorkManager's periodic scheduling with an initial delay calculation.
 *
 * Features:
 * - Scheduled to run around 7:00 AM East Africa Time daily.
 * - Requires network connectivity and non-low battery.
 * - Implements retry logic with linear backoff.
 * - Uses a single, resilient PeriodicWorkRequest for scheduling.
 *
 * @property repository The repository responsible for fetching the word data.
 */
@HiltWorker
class WordOfTheDayWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: WordOfTheDayRepository // Assuming this repository exists and is injectable
) : CoroutineWorker(ctx, params) {

    /**
     * Executes the worker's task to fetch the word of the day.
     *
     * This method runs on a background thread and attempts to fetch the
     * latest word of the day from the repository. It handles errors and
     * determines whether to report success or schedule a retry. The worker
     * no longer needs to reschedule itself; WorkManager handles periodic runs.
     *
     * @return Result indicating success, failure, or retry.
     */
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("WordOfTheDayWorker [$id]: Starting work")
                val result = repository.forceRefreshTodayWord()

                if (result.isSuccess) {
                    Timber.d("WordOfTheDayWorker [$id]: Successfully fetched word of the day")
                    Result.success()
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown repository error"
                    Timber.w("WordOfTheDayWorker [$id]: Failed to fetch word: $errorMessage. Will retry.")
                    Result.retry()
                }
            } catch (e: Exception) {
                Timber.e(e, "WordOfTheDayWorker [$id]: Exception occurred during work")
                Result.retry() // Retry on unexpected exceptions
            }
        }
    }

    companion object {
        /** Unique identifier for the daily periodic work request. */
        private const val WORK_NAME = "word_of_the_day_periodic_worker"

        /** Time zone used for scheduling (East Africa Time). */
        private const val TIME_ZONE = "Africa/Nairobi" // EAT Timezone ID

        /** Hour of day (in 24-hour format) when the word should ideally be fetched. */
        private const val FETCH_HOUR = 7 // 7:00 AM

        /** Delay between retry attempts in minutes. */
        private const val BACKOFF_DELAY_MINUTES = 15L

        /**
         * Schedules the daily periodic work request to fetch the word of the day.
         *
         * This should be called once during app startup (e.g., in the Application class).
         * It calculates the initial delay to target the next 7:00 AM EAT and then
         * schedules the work to repeat approximately every 24 hours.
         *
         * @param context Application context used to access WorkManager.
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun scheduleDailyWordFetchWork(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val constraints = createWorkConstraints()
            val initialDelay = calculateInitialDelay()

            Timber.d(
                "Scheduling periodic word fetch work '$WORK_NAME' with initial delay: " +
                        "${initialDelay / 1000 / 60} minutes (~${initialDelay / 1000 / 3600} hours)"
            )

            // Create a periodic work request
            val periodicRequest = PeriodicWorkRequestBuilder<WordOfTheDayWorker>(
                repeatInterval = 24, // Repeat every 24 hours
                repeatIntervalTimeUnit = TimeUnit.HOURS
                // Optional: Add flex time if some flexibility is acceptable/desirable
                // .setFlexTimeInterval(15, TimeUnit.MINUTES)
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Set the calculated delay for the first run
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    BACKOFF_DELAY_MINUTES,
                    TimeUnit.MINUTES
                )
                .build()

            // Enqueue the work uniquely, keeping the existing schedule if it's already running.
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )

            Timber.d("Enqueued unique periodic work '$WORK_NAME'. Policy: KEEP.")
        }

        /**
         * Creates common work constraints for the scheduling.
         * Requires network connectivity and battery not low.
         *
         * @return Constraints object for the WorkRequest.
         */
        private fun createWorkConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        }

        /**
         * Calculates the delay in milliseconds until the next scheduled run time (7:00 AM EAT)
         * using the modern java.time API.
         *
         * NOTE: Requires core library desugaring enabled in build.gradle if minSdk < 26.
         *
         * @return Delay in milliseconds until the next 7:00 AM EAT.
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun calculateInitialDelay(): Long {
            return try {
                val timeZone = ZoneId.of(TIME_ZONE)
                val now = ZonedDateTime.now(timeZone)
                val targetTime = LocalTime.of(FETCH_HOUR, 0) // 7:00 AM

                // Get 7:00 AM for today in the specified timezone
                var nextScheduledTime = now.with(targetTime)

                // If 7:00 AM today has already passed, schedule for 7:00 AM tomorrow
                if (now.isAfter(nextScheduledTime)) {
                    nextScheduledTime = nextScheduledTime.plusDays(1)
                }

                // Calculate the duration between now and the next scheduled time
                val delay = Duration.between(now, nextScheduledTime).toMillis()

                Timber.d("Calculated initial delay: ${delay}ms. Current time: $now, Next run target: $nextScheduledTime")

                // Ensure delay is not negative (shouldn't happen with correct logic, but safety check)
                if (delay < 0) 0 else delay
            } catch (e: Exception) {
                // Fallback in case of ZoneId issues or other time calculation errors
                Timber.e(e, "Error calculating initial delay using java.time. Scheduling for immediate run (or default WorkManager delay).")
                0L // Default to a 0ms delay (run as soon as constraints are met)
            }
        }
    }
}
