package com.example.myswimsmartdb.db.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Anwesenheit(
    private var _trainingDatum: String, // Store the date as a String
    val bemerkung: String,
    val anwesenheit: Boolean
) {
    var trainingDatum: String
        get() = _trainingDatum
        set(value) {
            _trainingDatum = value
            trainingDatumAsDate = stringToDate(value)
        }

    var trainingDatumAsDate: Date? = stringToDate(_trainingDatum)
        get() = stringToDate(_trainingDatum)
        set(value) {
            _trainingDatum = dateToString(value)
            field = value
        }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Funktion zur Konvertierung von Date zu String
        fun dateToString(date: Date?): String {
            return date?.let { dateFormat.format(it) } ?: ""
        }

        // Funktion zur Konvertierung von String zu Date
        fun stringToDate(dateString: String): Date? {
            return try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }
    }
}
