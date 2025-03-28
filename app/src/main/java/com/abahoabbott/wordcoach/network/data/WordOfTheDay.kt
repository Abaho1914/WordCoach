package com.abahoabbott.wordcoach.network.data

import kotlinx.serialization.Serializable

/**-----------------------------------
 * Network Data Transfer Objects (DTOs)
 * -----------------------------------*/
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

/**
 * Represents a content provider from the Wordnik API
 * @property name of the content provider
 * @property id Unique identifier from the API
 */
data class ContentProvider(
    val name: String,
    val id: Int
)


/**
 * Dictionary definition with source attribution
 * @property source Dictionary or source identifier
 * @property text Plain text definition
 * @property note Optional editorial note
 * @property partOfSpeech Grammatical classification
 */
@Serializable
data class Definition(
    val source: String,
    val text: String,
    val note: String?,
    val partOfSpeech: String
)


/**
 * Example usage from external sources
 * @property url Original content URL
 * @property title Title of source content
 * @property text Actual example text
 * @property id Unique identifier from API
 */
data class Example(
    val url: String,
    val title: String,
    val text: String,
    val id: Long
)