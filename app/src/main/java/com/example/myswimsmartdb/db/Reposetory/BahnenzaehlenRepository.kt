package com.example.myswimsmartdb.db.Reposetory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.myswimsmartdb.db.DatabaseHelper
import com.example.myswimsmartdb.db.entities.Bahnenzaehlen

class BahnenzaehlenRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertBahnenzaehlen(bahnenzaehlen: Bahnenzaehlen): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", bahnenzaehlen.id)
            put("mitgliedId", bahnenzaehlen.mitgliedId)
            put("vorname", bahnenzaehlen.vorname)
            put("nachname", bahnenzaehlen.nachname)
            put("bahnen", bahnenzaehlen.bahnen)
            put("bahnlaenge", bahnenzaehlen.bahnlaenge)
            put("zeitMode", bahnenzaehlen.zeitMode)
            put("zeit", bahnenzaehlen.zeit)
            put("running", if (bahnenzaehlen.running) 1 else 0)
            put("datumString", bahnenzaehlen.datumString)
        }
        return db.insert(DatabaseHelper.TABLE_BAHNENZAEHLEN, null, values)
    }

    fun deleteBahnenzaehlenByMitgliedId(mitgliedId: Int): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_BAHNENZAEHLEN, "mitgliedId = ?", arrayOf(mitgliedId.toString()))
    }

    fun deleteBahnenzaehlenById(id: Int): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_BAHNENZAEHLEN, "id = ?", arrayOf(id.toString()))
    }

    fun getBahnenzaehlenByMitgliedId(mitgliedId: Int): List<Bahnenzaehlen> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_BAHNENZAEHLEN,
            null,
            "MITGLIED_ID = ?",
            arrayOf(mitgliedId.toString()),
            null,
            null,
            null
        )

        val bahnenzaehlenList = mutableListOf<Bahnenzaehlen>()
        with(cursor) {
            while (moveToNext()) {
                val bahnenzaehlen = Bahnenzaehlen(
                    id = getInt(getColumnIndexOrThrow("BAHNENZAEHLEN_ID")),
                    mitgliedId = getInt(getColumnIndexOrThrow("MITGLIED_ID")),
                    vorname = getString(getColumnIndexOrThrow("VORNAME")),
                    nachname = getString(getColumnIndexOrThrow("NACHNAME")),
                    datumString = getString(getColumnIndexOrThrow("DATUMSTRING")),
                    bahnen = getInt(getColumnIndexOrThrow("BAHNEN")),
                    bahnlaenge = getInt(getColumnIndexOrThrow("BAHNLAENGE")),
                    zeitMode = getString(getColumnIndexOrThrow("ZEITMODE")),
                    zeit = getLong(getColumnIndexOrThrow("ZEIT")),
                    running = getInt(getColumnIndexOrThrow("RUNNING")) == 1
                )
                bahnenzaehlenList.add(bahnenzaehlen)
            }
        }
        cursor.close()
        return bahnenzaehlenList
    }
}
