package com.example.myswimsmartdb.db.Reposetory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.myswimsmartdb.db.DatabaseHelper
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
            put("id", stoppuhr.id)
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
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_STOPPUHR,
            null,
            "MITGLIED_ID = ?",
            arrayOf(mitgliedId.toString()),
            null,
            null,
            null
        )

        val stoppuhren = mutableListOf<Stoppuhr>()
        with(cursor) {
            while (moveToNext()) {
                val stoppuhr = Stoppuhr(
                    id = getInt(getColumnIndexOrThrow("STOPPUHR_ID")),
                    mitgliedId = getInt(getColumnIndexOrThrow("MITGLIED_ID")),
                    vorname = getString(getColumnIndexOrThrow("VORNAME")),
                    nachname = getString(getColumnIndexOrThrow("NACHNAME")),
                    zeit = getLong(getColumnIndexOrThrow("ZEIT")),
                    running = getInt(getColumnIndexOrThrow("RUNNING")) == 1,
                    datumString = getString(getColumnIndexOrThrow("DATUMSTRING")),
                    bemerkung = getString(getColumnIndexOrThrow("BEMERKUNG")),
                    schwimmarten = getString(getColumnIndexOrThrow("SCHWIMMARTEN")).split(","),
                    datum = dateFormat.parse(getString(getColumnIndexOrThrow("DATUM")))
                )
                stoppuhren.add(stoppuhr)
            }
        }
        cursor.close()
        return stoppuhren
    }
}
