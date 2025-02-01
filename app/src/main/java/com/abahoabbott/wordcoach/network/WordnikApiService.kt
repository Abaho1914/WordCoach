package com.abahoabbott.wordcoach.network

import com.abahoabbott.wordcoach.BuildConfig
import com.abahoabbott.wordcoach.network.data.WordOfTheDayResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//Used BuildConfig.WORDNIK_API_KEY to hide the api key
private const val WORDNIK_API_KEY = BuildConfig.WORDNIK_API_KEY

private const val BASE_URL_MODIFIED = "https://api.wordnik.com/v4/"

//Logging interceptor for debugging
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else HttpLoggingInterceptor.Level.NONE
}

//OkHttpClient with API key and logging interceptor
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .addInterceptor { chain ->
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", WORDNIK_API_KEY)
            .build()
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }
    .build()


//Retrofit with Gson Converter
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL_MODIFIED)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create(Gson()))
    .build()


data class RandomWordResponse(
    val word: String
)

interface WordnikApiService {
    @GET("words.json/wordOfTheDay")
    suspend fun getWordOfTheDay(): WordOfTheDayResponse

    @GET("words.json/randomWord?")
    suspend fun getRandomWord(): RandomWordResponse


}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object WordnikApi {
    val retrofitService: WordnikApiService by lazy {
        retrofit.create(WordnikApiService::class.java)
    }
}

