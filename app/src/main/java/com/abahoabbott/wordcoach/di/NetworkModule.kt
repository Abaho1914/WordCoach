package com.abahoabbott.wordcoach.di

import com.abahoabbott.wordcoach.BuildConfig
import com.abahoabbott.wordcoach.network.WordnikApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


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


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_MODIFIED)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWordnikApiService(retrofit: Retrofit): WordnikApiService {
        return retrofit.create(WordnikApiService::class.java)
    }

}