package com.example.myswimsmartdb.db

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateConverter {

    companion object {
        private val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        @JvmStatic
        fun dateToString(date: Date?): String {
            return date?.let { format.format(it) } ?: ""
        }

        @JvmStatic
        fun stringToDate(dateString: String): Date? {
            return try {
                format.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }
    }
}
