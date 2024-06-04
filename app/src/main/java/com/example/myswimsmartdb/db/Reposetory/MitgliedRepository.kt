package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Mitglied

class MitgliedRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun getMitgliederByKursId(kursId: Int): List<Mitglied> {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED} WHERE MITGLIED_KURS_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(kursId.toString()))

        val mitglieder = mutableListOf<Mitglied>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_ID"))
            val vorname = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_VORNAME"))
            val nachname = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_NACHNAME"))
            val geburtsdatum = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_GEBURTSDATUM"))
            val telefon = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_TELEFON"))

            mitglieder.add(Mitglied(id, vorname, nachname, geburtsdatum, telefon, kursId))
        }
        cursor.close()

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
}
