package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import com.example.myswimsmartdb.db.entities.Aufgabe

class AufgabeRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getAufgabeById(aufgabeId: Int): Aufgabe? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_AUFGABE,
            null,
            "AUFGABE_ID = ?",
            arrayOf(aufgabeId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("AUFGABE_ID"))
            val erledigt = cursor.getInt(cursor.getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
            val text = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
            val beschreibung = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))
            cursor.close()
            Aufgabe(id, erledigt, text, beschreibung)
        } else {
            cursor.close()
            null
        }
    }

    fun getAufgabenByLevelId(levelId: Int): List<Aufgabe> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.*
            FROM ${DatabaseHelper.TABLE_AUFGABE} a
            INNER JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} la
            ON a.AUFGABE_ID = la.LEVEL_AUFGABE_AUFGABE_ID
            WHERE la.LEVEL_AUFGABE_LEVEL_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(levelId.toString()))

        val aufgaben = mutableListOf<Aufgabe>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("AUFGABE_ID"))
            val erledigt = cursor.getInt(cursor.getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
            val text = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
            val beschreibung = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))
            aufgaben.add(Aufgabe(id, erledigt, text, beschreibung))
        }
        cursor.close()

        return aufgaben
    }
}
