package com.abahoabbott.wordcoach.room.dictionary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("words_dictionary")
data class WordCoachDictionary(
    @PrimaryKey
    val id: Int,
    val word: String,
    val definition: String,
    val example: String
)