package com.abahoabbott.wordcoach.network.data

import kotlinx.serialization.Serializable

// Data classes for API responses
data class WordOfTheDayResponse(
    val _id: String,
    val word: String,
    val contentProvider: ContentProvider,
    val definitions: List<Definition>,
    val publishDate: String,
    val examples: List<Example>,
    val pdd: String,
    val htmlExtra: String?,
    val note: String
)

data class ContentProvider(
    val name: String,
    val id: Int
)

@Serializable
data class Definition(
    val source: String,
    val text: String,
    val note: String?,
    val partOfSpeech: String
)


data class Example(
    val url: String,
    val title: String,
    val text: String,
    val id: Long
)