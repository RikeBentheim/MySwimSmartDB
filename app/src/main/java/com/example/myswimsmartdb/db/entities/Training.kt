package com.example.myswimsmartdb.db.entities

import android.content.Context
import com.example.myswimsmartdb.db.DatabaseHelper
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

    // Function to get Anwesenheit by Mitglied and Training
    fun getAnwesenheitByMitgliedAndTraining(context: Context, mitgliedId: Int, trainingId: Int): Anwesenheit? {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_ANWESENHEIT}
            WHERE ANWESENHEIT_MITGLIED_ID = ? AND ANWESENHEIT_TRAINING_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(mitgliedId.toString(), trainingId.toString()))

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("ANWESENHEIT_ID"))
            val bemerkung = cursor.getString(cursor.getColumnIndexOrThrow("ANWESENHEIT_BEMERKUNG"))
            val anwesenheit = cursor.getInt(cursor.getColumnIndexOrThrow("ANWESENHEIT_ANWESEND")) > 0
            cursor.close()
            Anwesenheit(id, mitgliedId, trainingId, "", bemerkung, anwesenheit)
        } else {
            cursor.close()
            null
        }
    }
}
