package com.abahoabbott.wordcoach.features.wod.work

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.abahoabbott.wordcoach.features.wod.ErrorType
import com.abahoabbott.wordcoach.features.wod.WordOfTheDayState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewViewModel @Inject constructor(
    private val repository: WordOfTheDayRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _wordOfTheDayState = MutableStateFlow<WordOfTheDayState>(WordOfTheDayState.Loading)
    val wordOfTheDayState: StateFlow<WordOfTheDayState> = _wordOfTheDayState.asStateFlow()


    init {
        scheduleWordFetch()
        observeLatestWord()
    }

    private fun scheduleWordFetch() {
        //create work request to run daily at midnight
        val currentDate = Calendar.getInstance()
        val nextRun = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val initialDelay = nextRun.timeInMillis - currentDate.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WordOfTheDayWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WordOfTheDayWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Log.i("WordCoach", "Scheduled")

    }

    private fun observeLatestWord() {

        viewModelScope.launch {
            repository.getLatestWord().collect { word ->
                _wordOfTheDayState.value = if (word != null) {
                    Log.i("WordCoach",word.word)
                    WordOfTheDayState.Success(
                        wordOfTheDay = word,
                        isFromCache = true,
                        lastUpdated = System.currentTimeMillis()
                    )

                } else {
                    Log.i("WordCoach", "No word" )
                    WordOfTheDayState.Error(
                        message = "No word found",
                        type = ErrorType.LOCAL_DATA_ERROR
                    )
                }
            }
        }

    }

}