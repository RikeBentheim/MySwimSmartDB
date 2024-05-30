package com.example.myswimsmartdb.db.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Training(
    val id: Int,
    val datumString: String, // Store the date as a String
    val bemerkung: String
) {

    var datum: String = datumString
        set(value) {
            field = value
            datumAsDate = stringToDate(value)
        }

    var datumAsDate: Date? = stringToDate(datumString)
        private set

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Funktion zur Konvertierung von Date zu String
        private fun dateToString(date: Date?): String {
            return date?.let { dateFormat.format(it) } ?: ""
        }

        // Funktion zur Konvertierung von String zu Date
        private fun stringToDate(dateString: String): Date? {
            return try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }
    }
}
