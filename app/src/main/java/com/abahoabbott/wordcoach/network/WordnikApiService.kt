package com.abahoabbott.wordcoach.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val WORDNIK_API_KEY = "yhy01zva8d2wel6dpn45dcy6l1y8ppbwp47vagxf41v3m1rqf"

private const val BASE_URL_MODIFIED = "http://api.wordnik.com/v4"


private const val BASE_URL =
    "https://android-kotlin-fun-mars-server.appspot.com"


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

data class RandomWordResponse(
    val word: String
)

interface WordnikApiService {
    @GET("/words.json/wordOfTheDay")
    suspend fun getWordOfTheDay(@Query("api_key") apiKey: String = WORDNIK_API_KEY): String

    @GET("words.json/randomWord")
    suspend fun getRandomWord(
        @Query("api_key") apiKey: String = WORDNIK_API_KEY
    ): Response<RandomWordResponse>


    @GET("photos")
   suspend fun getPhotos(): String
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object WordnikApi {
    val retrofitService: WordnikApiService by lazy {
        retrofit.create(WordnikApiService::class.java)
    }
}

