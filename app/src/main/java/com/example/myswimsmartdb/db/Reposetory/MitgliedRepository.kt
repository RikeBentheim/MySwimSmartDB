package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.MitgliedAufgabe

class MitgliedRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getMitgliederByKursId(kursId: Int): List<Mitglied> {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED} WHERE MITGLIED_KURS_ID = ?"
        val mitglieder = mutableListOf<Mitglied>()

        db.rawQuery(query, arrayOf(kursId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_ID"))
                val vorname = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_VORNAME"))
                val nachname = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_NACHNAME"))
                val geburtsdatum = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_GEBURTSDATUM"))
                val telefon = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_TELEFON"))

                mitglieder.add(Mitglied(id, vorname, nachname, geburtsdatum, telefon, kursId))
            }
        }

        // Load tasks for each member
        mitglieder.forEach { mitglied ->
            mitglied.aufgaben = getAufgabenByKursId(mitglied.kursId)
        }

        return mitglieder
    }

    fun insertMitgliedWithAufgaben(mitglied: Mitglied, aufgaben: List<Aufgabe>): Long {
        val db = dbHelper.writableDatabase
        val mitgliedValues = ContentValues().apply {
            put("MITGLIED_VORNAME", mitglied.vorname)
            put("MITGLIED_NACHNAME", mitglied.nachname)
            put("MITGLIED_GEBURTSDATUM", mitglied.geburtsdatum)
            put("MITGLIED_TELEFON", mitglied.telefon)
            put("MITGLIED_KURS_ID", mitglied.kursId)
        }
        val mitgliedId = db.insert(DatabaseHelper.TABLE_MITGLIED, null, mitgliedValues)

        if (mitgliedId != -1L) {
            for (aufgabe in aufgaben) {
                val mitgliedAufgabeValues = ContentValues().apply {
                    put("MITGLIED_AUFGABE_MITGLIED_ID", mitgliedId)
                    put("MITGLIED_AUFGABE_AUFGABE_ID", aufgabe.id)
                    put("ERREICHT", 0) // Default value for the new column
                }
                db.insert(DatabaseHelper.TABLE_MITGLIED_AUFGABE, null, mitgliedAufgabeValues)
            }
        }
        return mitgliedId
    }

    fun updateMitglied(mitglied: Mitglied): Int {
        val db = dbHelper.writableDatabase
        val mitgliedValues = ContentValues().apply {
            put("MITGLIED_VORNAME", mitglied.vorname)
            put("MITGLIED_NACHNAME", mitglied.nachname)
            put("MITGLIED_GEBURTSDATUM", mitglied.geburtsdatum)
            put("MITGLIED_TELEFON", mitglied.telefon)
            put("MITGLIED_KURS_ID", mitglied.kursId)
        }
        return db.update(DatabaseHelper.TABLE_MITGLIED, mitgliedValues, "MITGLIED_ID = ?", arrayOf(mitglied.id.toString()))
    }

    fun deleteMitglied(mitgliedId: Int) {
        val db = dbHelper.writableDatabase

        // Delete from TABLE_ANWESENHEIT
        db.delete(DatabaseHelper.TABLE_ANWESENHEIT, "ANWESENHEIT_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

        // Delete from TABLE_MITGLIED_AUFGABE
        db.delete(DatabaseHelper.TABLE_MITGLIED_AUFGABE, "MITGLIED_AUFGABE_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

        // Delete from TABLE_MITGLIED
        db.delete(DatabaseHelper.TABLE_MITGLIED, "MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))
    }

    fun updateMitgliedAufgabeErreicht(mitgliedId: Int, aufgabeId: Int, erreicht: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("ERREICHT", if (erreicht) 1 else 0)
        }
        db.update(
            DatabaseHelper.TABLE_MITGLIED_AUFGABE,
            values,
            "MITGLIED_AUFGABE_MITGLIED_ID = ? AND MITGLIED_AUFGABE_AUFGABE_ID = ?",
            arrayOf(mitgliedId.toString(), aufgabeId.toString())
        )
    }

    fun getMitgliedAufgabenByAufgabeId(aufgabeId: Int): List<MitgliedAufgabe> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED_AUFGABE}
            WHERE MITGLIED_AUFGABE_AUFGABE_ID = ?
        """
        val mitgliedAufgaben = mutableListOf<MitgliedAufgabe>()

        db.rawQuery(query, arrayOf(aufgabeId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                val mitgliedAufgabeId = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_AUFGABE_ID"))
                val mitgliedId = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_AUFGABE_MITGLIED_ID"))
                val erreicht = cursor.getInt(cursor.getColumnIndexOrThrow("ERREICHT")) > 0
                mitgliedAufgaben.add(MitgliedAufgabe(mitgliedAufgabeId, mitgliedId, aufgabeId, erreicht))
            }
        }

        return mitgliedAufgaben
    }

    fun getAufgabenByKursId(kursId: Int): List<Aufgabe> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT A.* FROM ${DatabaseHelper.TABLE_AUFGABE} A
            JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} LA
            ON A.AUFGABE_ID = LA.LEVEL_AUFGABE_AUFGABE_ID
            JOIN ${DatabaseHelper.TABLE_KURS} K
            ON LA.LEVEL_AUFGABE_LEVEL_ID = K.KURS_LEVEL_ID
            WHERE K.KURS_ID = ?
        """
        val aufgaben = mutableListOf<Aufgabe>()

        db.rawQuery(query, arrayOf(kursId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("AUFGABE_ID"))
                val erledigt = cursor.getInt(cursor.getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
                val text = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
                val beschreibung = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))
                aufgaben.add(Aufgabe(id, erledigt, text, beschreibung))
            }
        }

        return aufgaben
    }
    fun getAufgabeTextById(aufgabeId: Int): String {
        val db = dbHelper.readableDatabase
        val query = "SELECT AUFGABE_TEXT FROM ${DatabaseHelper.TABLE_AUFGABE} WHERE AUFGABE_ID = ?"
        var aufgabeText = ""

        db.rawQuery(query, arrayOf(aufgabeId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                aufgabeText = cursor.getString(cursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
            }
        }

        return aufgabeText
    }
}
