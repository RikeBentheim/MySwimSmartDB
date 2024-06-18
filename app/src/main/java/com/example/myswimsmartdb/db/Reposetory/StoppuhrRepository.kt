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
            "mitgliedId = ?",
            arrayOf(mitgliedId.toString()),
            null,
            null,
            null
        )

        val stoppuhren = mutableListOf<Stoppuhr>()
        with(cursor) {
            while (moveToNext()) {
                val stoppuhr = Stoppuhr(
                    id = getInt(getColumnIndexOrThrow("id")),
                    mitgliedId = getInt(getColumnIndexOrThrow("mitgliedId")),
                    vorname = getString(getColumnIndexOrThrow("vorname")),
                    nachname = getString(getColumnIndexOrThrow("nachname")),
                    zeit = getLong(getColumnIndexOrThrow("zeit")),
                    running = getInt(getColumnIndexOrThrow("running")) == 1,
                    datumString = getString(getColumnIndexOrThrow("datumString")),
                    bemerkung = getString(getColumnIndexOrThrow("bemerkung")),
                    schwimmarten = getString(getColumnIndexOrThrow("schwimmarten")).split(","),
                    datum = dateFormat.parse(getString(getColumnIndexOrThrow("datum")))
                )
                stoppuhren.add(stoppuhr)
            }
        }
        cursor.close()
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
