package com.abahoabbott.wordcoach.common

import android.util.Log
import androidx.compose.runtime.remember
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
* Interface for date formatting operations to improve testability
*/
interface DateFormatter {
    fun getCurrentDate(): String
    fun parseApiDate(apiDate: String): String?
    fun parseIsoDate(isoDateString: String): Date
    fun convertToFixedTimeFormat(dateStr: String): String?
}

class SimpleDateFormatter(): DateFormatter {

    private val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    override fun getCurrentDate(): String {
        return currentDateFormat.format(Date())
    }
    /**
     * Parses an ISO 8601 formatted date string into a Date object
     *
     * @param isoDateString The date string in ISO 8601 format (e.g., "2023-03-01T00:00:00Z")
     * @return Date object representing the parsed date
     */
    override fun parseIsoDate(isoDateString: String): Date {
        return try {
            // Option 1: Using SimpleDateFormat (API level 1+)
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(isoDateString) ?: Date()

            // Option 2: Using DateTimeFormatter with Java 8+ API (API level 26+)
            // Uncomment this and comment out the SimpleDateFormat option if using API 26+
            /*
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val zonedDateTime = ZonedDateTime.parse(isoDateString, formatter)
            Date.from(zonedDateTime.toInstant())
            */
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error parsing date: $isoDateString", e)
            // Return current date as fallback
            Date()
        }
    }



    override fun parseApiDate(apiDate: String): String? {
        return try {
            apiDateFormat.parse(apiDate)?.let(currentDateFormat::format)
        } catch (e: ParseException) {
            Log.e("DateFormatter", "Failed to parse date", e)
            null
        }
    }

    override fun convertToFixedTimeFormat(dateStr: String): String?{
        return try {
            // Parse input date in UTC timezone
            val parsedDate = currentDateFormat.parse(dateStr)?: return null

            // Create calendar instance in UTC
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                time = parsedDate
                // Set fixed time components
                set(Calendar.HOUR_OF_DAY, 3)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            // Format with fixed time component
            apiDateFormat.format(calendar.time)
        } catch (e: ParseException) {
            Log.e("DateFormatter", "Invalid date format: $dateStr", e)
            null
        }
        }
}