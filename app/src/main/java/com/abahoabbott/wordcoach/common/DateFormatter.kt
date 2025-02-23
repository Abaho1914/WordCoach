package com.abahoabbott.wordcoach.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
* Interface for date formatting operations to improve testability
*/
interface DateFormatter {
    fun getCurrentDate(): String
    fun parseApiDate(apiDate: String): String
}

class SimpleDateFormatter(): DateFormatter{
    private val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    override fun getCurrentDate(): String {
        return currentDateFormat.format(Date())
    }

    override fun parseApiDate(apiDate: String): String {
        return apiDateFormat.parse(apiDate)?.let(currentDateFormat::format) ?: ""
    }
}