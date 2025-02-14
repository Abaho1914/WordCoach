package com.abahoabbott.wordcoach.features.wod.work

import com.abahoabbott.wordcoach.features.wod.DataStoreManager
import com.abahoabbott.wordcoach.features.wod.WordOfTheDay
import com.abahoabbott.wordcoach.features.wod.toWordOfTheDay
import com.abahoabbott.wordcoach.network.WordnikApiService
import kotlinx.coroutines.flow.Flow

class WordOfTheDayRepository(
    private val dataStoreManager: DataStoreManager,
    private val wordnikApiService: WordnikApiService
) {
    suspend fun fetchWordOfDay(): Result<WordOfTheDay> =
        runCatching {
            val response = wordnikApiService.getWordOfTheDay().toWordOfTheDay()
            //save word to database or dataStorage
            dataStoreManager.saveWordData(response, System.currentTimeMillis())
            response
        }

    fun getLatestWord(): Flow<WordOfTheDay?> = dataStoreManager.lastWord

}