package com.abahoabbott.wordcoach.features.wod.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WordOfTheDayWorker @AssistedInject constructor(
    @Assisted  ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: WordOfTheDayRepository
) :
    CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
                repository.fetchWordOfDay()
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "word_fetch_worker"
    }
}