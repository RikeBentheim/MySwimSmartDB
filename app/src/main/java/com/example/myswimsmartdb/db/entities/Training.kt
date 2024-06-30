package com.example.myswimsmartdb.db.entities

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.example.myswimsmartdb.db.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Training(
    val id: Int,
    val datumString: String, // Store the date as a String
    val bemerkung: String
) : Parcelable {

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

        @JvmField
        val CREATOR: Parcelable.Creator<Training> = object : Parcelable.Creator<Training> {
            override fun createFromParcel(parcel: Parcel): Training {
                return Training(parcel)
            }

            override fun newArray(size: Int): Array<Training?> {
                return arrayOfNulls(size)
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

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
        datum = parcel.readString() ?: datumString
        datumAsDate = stringToDate(datum)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(datumString)
        parcel.writeString(bemerkung)
        parcel.writeString(datum)
    }

    override fun describeContents(): Int {
        return 0
    }
}
