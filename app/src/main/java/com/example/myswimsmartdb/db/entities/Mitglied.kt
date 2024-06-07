package com.example.myswimsmartdb.db.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Mitglied(
    val id: Int,
    val vorname: String,
    val nachname: String,
    val geburtsdatumString: String, // Store the date as a String
    val telefon: String,
    val kursId: Int,
    var anwesenheiten: List<Anwesenheit> = emptyList(),
    var aufgaben: List<Aufgabe> = emptyList()
) {

    var geburtsdatum: String = geburtsdatumString
        set(value) {
            field = value
            geburtsdatumAsDate = stringToDate(value)
        }

    var geburtsdatumAsDate: Date? = stringToDate(geburtsdatum)
        get() = stringToDate(geburtsdatum)
        set(value) {
            field = value
            geburtsdatum = dateToString(value)
        }

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
