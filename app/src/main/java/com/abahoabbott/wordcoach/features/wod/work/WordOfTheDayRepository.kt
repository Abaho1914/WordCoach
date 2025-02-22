package com.abahoabbott.wordcoach.features.wod.work

import android.util.Log
import com.abahoabbott.wordcoach.common.LOG_TAG
import com.abahoabbott.wordcoach.features.wod.DataStoreManager
import com.abahoabbott.wordcoach.features.wod.WordOfTheDay
import com.abahoabbott.wordcoach.features.wod.toWordOfTheDay
import com.abahoabbott.wordcoach.network.WordnikApiService
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Updated WordOfTheDayRepository
class WordOfTheDayRepository(
    private val dataStoreManager: DataStoreManager,
    private val wordnikApiService: WordnikApiService
) {
    /**
     * Retrieves the word of the day using a smart caching strategy with network fallback.
     *
     * Operates in a priority sequence:
     * 1. Fresh cache (if available and valid)
     * 2. Network fetch (when required)
     * 3. Stale cache (if network fails)
     *
     * @param forceFetch When true:
     *   - Bypasses all cache checks
     *   - Forces a fresh network request
     *   - Still falls back to cache if network fails
     *   Default: false (respect cache validity)
     *
     * @return Result<WordOfTheDay> with:
     * - Success containing valid word if:
     *   • Current cache exists (when allowed), OR
     *   • Network fetch succeeds, OR
     *   • Network fails but previous cache exists
     * - Failure only when:
     *   • Network request fails AND no cached data exists
     *
     * ### Behavior Details
     *
     * 1. **Cache Validation Check**
     *    - Checks `forceFetch` flag first
     *    - Verifies cache freshness via `shouldFetchNewWord()`
     *    - Immediately returns valid cache if available
     *
     *    ```kotlin
     *    if (!shouldFetch) {
     *        // Return cached word if exists
     *    }
     *    ```
     *
     * 2. **Network Request Attempt**
     *    - Makes API call to fetch fresh data
     *    - Converts API response to domain model
     *    - Persists successful response to cache
     *
     *    ```kotlin
     *    val response = wordnikApiService.getWordOfTheDay().toWordOfTheDay()
     *    dataStoreManager.saveWordData(response)
     *    ```
     *
     * 3. **Error Recovery**
     *    - On any network exceptions:
     *      - Attempts to return existing cache
     *      - Returns failure ONLY if cache is empty
     *
     *    ```kotlin
     *    catch (e: Exception) {
     *        dataStoreManager.lastWord.first()?.let {
     *            Result.success(it)
     *        } ?: Result.failure(e)
     *    }
     *    ```
     *
     * ### Usage Examples
     * 1. Normal cache-first usage:
     * ```kotlin
     * repository.fetchWordOfDay() // Respects cache validity
     * ```
     *
     * 2. Force refresh:
     * ```kotlin
     * repository.fetchWordOfDay(forceFetch = true) // Bypasses cache
     * ```
     */
    suspend fun fetchWordOfDay(forceFetch: Boolean = false): Result<WordOfTheDay> {
        Log.i(LOG_TAG, "WordOfTheDayRepository:Fetching word of the day")
        val shouldFetch = forceFetch || shouldFetchNewWord() || isWordStale()

        if (!shouldFetch) {
            dataStoreManager.lastWord.first()?.let {
                Log.i(LOG_TAG, "WordOfTheDayRepository:Cached word of the day")
                return Result.success(it)
            }
        }

        return try {
            val response = wordnikApiService.getWordOfTheDay().toWordOfTheDay()
            Log.i(LOG_TAG, "WordOfTheDayRepository:API RESPONSE:$response")
            dataStoreManager.saveWordData(response)
            Result.success(response)
        } catch (e: Exception) {
            dataStoreManager.lastWord.first()?.let {
                Result.success(it)
            } ?: Result.failure(e)
        }
    }

    fun Long.logTime(
        pattern: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: String = "UTC"
    ): String {
        val date = Date(this)
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone(timeZone)
        return formatter.format(date)
    }

    /**
     * Determines if a new word should be fetched based on the last fetch time.
     *
     * - If no previous fetch exists, returns `true`.
     * - If a previous fetch exists, checks if the current time has passed the next eligible
     *   fetch time (next day at 7:00 AM in Nairobi timezone).
     *
     * @return `true` if a new word should be fetched, `false` otherwise.
     */
    private suspend fun shouldFetchNewWord(): Boolean {
        // Fetch the last saved timestamp from DataStore
        val lastFetch = dataStoreManager.lastFetchDate.first()
        Log.i(LOG_TAG, "Last fetch${lastFetch?.logTime()}")

        // If no previous fetch exists, trigger a new fetch
        return if (lastFetch == null) {
            Log.i(LOG_TAG, "First fetch required: no previous timestamp")
            true
        } else {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Nairobi")).apply {
                timeInMillis = lastFetch
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 7)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            Log.i(LOG_TAG, "Current time: ${System.currentTimeMillis().logTime()}")
            Log.i(LOG_TAG, "Calendar time:${calendar.timeInMillis.logTime()}")

            Log.i(
                LOG_TAG,
                "show fetch new word: ${System.currentTimeMillis() >= calendar.timeInMillis}"
            )
            System.currentTimeMillis() >= calendar.timeInMillis
        }
    }

    fun convertPublishDateToLong(publishDate: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC timezone parsing
        return try {
            val date: Date = format.parse(publishDate)
            date.time // Returns milliseconds since epoch
        } catch (e: Exception) {
            -1L // Handle error (e.g., invalid format)
        }
    }

    suspend fun isWordStale(): Boolean{
        //fetch current word in dataStore
        val lastWord = dataStoreManager.lastWord.first()
       return if (lastWord ==null){
            true
        } else{
            val publishDate = convertPublishDateToLong(lastWord.publishDate)
           publishDate < System.currentTimeMillis()
       }
    }
}