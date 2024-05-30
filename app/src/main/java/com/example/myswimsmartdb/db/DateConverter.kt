package com.example.myswimsmartdb.db

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateConverter {


    public companion object{
        fun dateToString(date: Date?): String {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return date?.let { format.format(it) } ?: ""
        }

        // Funktion zur Konvertierung von String zu Date
        fun stringToDate(dateString: String): Date? {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return try {
                format.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }

    }
}