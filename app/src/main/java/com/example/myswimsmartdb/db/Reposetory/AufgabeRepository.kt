package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Level

class AufgabeRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getAllAufgaben(): List<Aufgabe> {
        val db = dbHelper.readableDatabase
        val projection =
            arrayOf("AUFGABE_ID", "AUFGABE_ERLEDIGT", "AUFGABE_TEXT", "AUFGABE_BESCHREIBUNG")
        val cursor =
            db.query(DatabaseHelper.TABLE_AUFGABE, projection, null, null, null, null, null)

        val aufgaben = mutableListOf<Aufgabe>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("AUFGABE_ID"))
                val erledigt = getInt(getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
                val text = getString(getColumnIndexOrThrow("AUFGABE_TEXT"))
                val beschreibung = getString(getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))
                aufgaben.add(Aufgabe(id, erledigt, text, beschreibung))
            }
        }
        cursor.close()
        return aufgaben
    }

    fun insertAufgabe(aufgabe: Aufgabe, levelId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("AUFGABE_ERLEDIGT", if (aufgabe.erledigt) 1 else 0)
            put("AUFGABE_TEXT", aufgabe.aufgabe)
            put("AUFGABE_BESCHREIBUNG", aufgabe.beschreibung)
        }
        val aufgabeId = db.insert(DatabaseHelper.TABLE_AUFGABE, null, values)
        if (aufgabeId != -1L) {
            val levelAufgabeValues = ContentValues().apply {
                put("LEVEL_AUFGABE_AUFGABE_ID", aufgabeId)
                put("LEVEL_AUFGABE_LEVEL_ID", levelId)
            }
            db.insert(DatabaseHelper.TABLE_LEVEL_AUFGABE, null, levelAufgabeValues)
        }
        return aufgabeId
    }

    fun getAufgabenByLevelId(levelId: Int): List<Aufgabe> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.AUFGABE_ID, a.AUFGABE_ERLEDIGT, a.AUFGABE_TEXT, a.AUFGABE_BESCHREIBUNG 
            FROM ${DatabaseHelper.TABLE_AUFGABE} a
            INNER JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} la 
            ON a.AUFGABE_ID = la.LEVEL_AUFGABE_AUFGABE_ID 
            WHERE la.LEVEL_AUFGABE_LEVEL_ID = $levelId
        """
        val cursor = db.rawQuery(query, null)

        val aufgaben = mutableListOf<Aufgabe>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("AUFGABE_ID"))
                val erledigt = getInt(getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
                val text = getString(getColumnIndexOrThrow("AUFGABE_TEXT"))
                val beschreibung = getString(getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))
                aufgaben.add(Aufgabe(id, erledigt, text, beschreibung))
            }
        }
        cursor.close()
        return aufgaben
    }
}