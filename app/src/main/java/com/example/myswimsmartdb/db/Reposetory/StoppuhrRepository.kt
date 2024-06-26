package com.example.myswimsmartdb.db.Reposetory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.myswimsmartdb.db.DatabaseHelper
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Stoppuhr
import java.text.SimpleDateFormat
import java.util.*

class StoppuhrRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun insertStoppuhr(stoppuhr: Stoppuhr): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val currentDate = dateFormat.format(Date()) // Get the current date

        val values = ContentValues().apply {
            put("MITGLIED_ID", stoppuhr.mitgliedId)
            put("VORNAME", stoppuhr.vorname)
            put("NACHNAME", stoppuhr.nachname)
            put("ZEIT", stoppuhr.zeit)
            put("RUNNING", if (stoppuhr.running) 1 else 0)
            put("DATUMSTRING", stoppuhr.datumString)
            put("BEMERKUNG", stoppuhr.bemerkung)
            put("SCHWIMMARTEN", stoppuhr.schwimmarten.joinToString(","))
            put("DATUM", currentDate)
        }
        return db.insert(DatabaseHelper.TABLE_STOPPUHR, null, values)
    }

    fun deleteStoppuhrByMitgliedId(mitgliedId: Int): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_STOPPUHR, "mitgliedId = ?", arrayOf(mitgliedId.toString()))
    }

    fun deleteStoppuhrById(id: Int): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_STOPPUHR, "id = ?", arrayOf(id.toString()))
    }

    fun getStoppuhrenByMitgliedId(mitgliedId: Int): List<Stoppuhr> {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_STOPPUHR} WHERE MITGLIED_ID = ?"
        val stoppuhren = mutableListOf<Stoppuhr>()

        db.rawQuery(query, arrayOf(mitgliedId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("STOPPUHR_ID"))
                val vorname = cursor.getString(cursor.getColumnIndexOrThrow("VORNAME"))
                val nachname = cursor.getString(cursor.getColumnIndexOrThrow("NACHNAME"))
                val zeit = cursor.getLong(cursor.getColumnIndexOrThrow("ZEIT"))
                val running = cursor.getInt(cursor.getColumnIndexOrThrow("RUNNING")) > 0
                val datumString = cursor.getString(cursor.getColumnIndexOrThrow("DATUMSTRING"))
                val bemerkung = cursor.getString(cursor.getColumnIndexOrThrow("BEMERKUNG"))
                val schwimmarten = cursor.getString(cursor.getColumnIndexOrThrow("SCHWIMMARTEN")).split(",")
                val datum = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("DATUM")))

                stoppuhren.add(Stoppuhr(id, mitgliedId, vorname, nachname, datumString, zeit, running, bemerkung, schwimmarten, datum!!))
            }
        }

        return stoppuhren
    }

    fun saveAll(mitglieder: List<Mitglied>) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val currentDate = dateFormat.format(Date()) // Get the current date
        db.beginTransaction()
        try {
            mitglieder.forEach { mitglied ->
                val stoppuhr = Stoppuhr(
                    id = 0, // or some logic to generate a new ID
                    mitgliedId = mitglied.id,
                    vorname = mitglied.vorname,
                    nachname = mitglied.nachname,
                    zeit = 0L, // assuming you want to reset the time
                    running = false,
                    datumString = currentDate,
                    bemerkung = "",
                    schwimmarten = listOf()
                )
                val values = ContentValues().apply {
                    put("mitgliedId", stoppuhr.mitgliedId)
                    put("vorname", stoppuhr.vorname)
                    put("nachname", stoppuhr.nachname)
                    put("zeit", stoppuhr.zeit)
                    put("running", if (stoppuhr.running) 1 else 0)
                    put("datumString", stoppuhr.datumString)
                    put("bemerkung", stoppuhr.bemerkung)
                    put("schwimmarten", stoppuhr.schwimmarten.joinToString(","))
                    put("datum", currentDate) // Store the current date
                }
                db.insert(DatabaseHelper.TABLE_STOPPUHR, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
