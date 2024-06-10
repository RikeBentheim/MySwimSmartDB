package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.myswimsmartdb.db.entities.*

class MitgliedRepository(private val context: Context) {

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

        if (mitgliedId == -1L) {
            Log.e("DatabaseError", "Failed to insert Mitglied: $mitgliedValues")
            return -1
        }

        for (aufgabe in aufgaben) {
            val mitgliedAufgabeValues = ContentValues().apply {
                put("MITGLIED_AUFGABE_MITGLIED_ID", mitgliedId)
                put("MITGLIED_AUFGABE_AUFGABE_ID", aufgabe.id)
                put("ERREICHT", 0)
            }
            val result = db.insert(DatabaseHelper.TABLE_MITGLIED_AUFGABE, null, mitgliedAufgabeValues)
            if (result == -1L) {
                Log.e("DatabaseError", "Failed to insert MitgliedAufgabe: $mitgliedAufgabeValues")
                // Optional: Sie könnten hier entscheiden, ob Sie die gesamte Operation zurückrollen möchten
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

        db.delete(DatabaseHelper.TABLE_ANWESENHEIT, "ANWESENHEIT_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))
        db.delete(DatabaseHelper.TABLE_MITGLIED_AUFGABE, "MITGLIED_AUFGABE_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))
        db.delete(DatabaseHelper.TABLE_MITGLIED, "MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))
    }

    fun getMitgliedAufgabenByAufgabeId(aufgabeId: Int): List<MitgliedAufgabe> {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED_AUFGABE} WHERE MITGLIED_AUFGABE_AUFGABE_ID = ?"
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
            JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} LA ON A.AUFGABE_ID = LA.LEVEL_AUFGABE_AUFGABE_ID
            JOIN ${DatabaseHelper.TABLE_KURS} K ON LA.LEVEL_AUFGABE_LEVEL_ID = K.KURS_LEVEL_ID
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

    fun getFullMitgliederDetailsByKursId(kursId: Int): List<Mitglied> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT MITGLIED_ID, MITGLIED_VORNAME, MITGLIED_NACHNAME, MITGLIED_GEBURTSDATUM, MITGLIED_TELEFON, MITGLIED_KURS_ID
        FROM ${DatabaseHelper.TABLE_MITGLIED}
        WHERE MITGLIED_KURS_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(kursId.toString()))
        val mitglieder = mutableListOf<Mitglied>()

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val mitglied = Mitglied(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_ID")),
                        vorname = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_VORNAME")),
                        nachname = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_NACHNAME")),
                        geburtsdatumString = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_GEBURTSDATUM")),
                        telefon = cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_TELEFON")),
                        kursId = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_KURS_ID"))
                    )

                    // Aufgaben für das Mitglied laden
                    val aufgaben = getMitgliedAufgabenByMitgliedId(mitglied.id)
                    mitglied.aufgaben = aufgaben.map { aufgabe ->
                        Aufgabe(aufgabe.aufgabeId, aufgabe.erreicht, aufgabe.aufgabeId.toString(), "")
                    }

                    mitglieder.add(mitglied)
                } while (cursor.moveToNext())
            }
        }
        return mitglieder
    }


    fun updateMitgliedAufgabeErreicht(mitgliedId: Int, aufgabeId: Int, erreicht: Boolean) {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("ERREICHT", if (erreicht) 1 else 0)
        }

        val whereClause = "MITGLIED_AUFGABE_MITGLIED_ID = ? AND MITGLIED_AUFGABE_AUFGABE_ID = ?"
        val whereArgs = arrayOf(mitgliedId.toString(), aufgabeId.toString())

        Log.d("updateMitgliedAufgabeErreicht", "Update mitgliedId: $mitgliedId, aufgabeId: $aufgabeId, erreicht: $erreicht")

        val rowsUpdated = db.update(DatabaseHelper.TABLE_MITGLIED_AUFGABE, contentValues, whereClause, whereArgs)
        db.close()

        Log.d("updateMitgliedAufgabeErreicht", "Rows updated: $rowsUpdated")
    }

    fun getMitgliedAufgabe(mitgliedId: Int, aufgabeId: Int): MitgliedAufgabe? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED_AUFGABE}
        WHERE MITGLIED_AUFGABE_MITGLIED_ID = ? AND MITGLIED_AUFGABE_AUFGABE_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(mitgliedId.toString(), aufgabeId.toString()))

        return if (cursor.moveToFirst()) {
            val mitgliedAufgabeId = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_AUFGABE_ID"))
            val erreicht = cursor.getInt(cursor.getColumnIndexOrThrow("ERREICHT")) > 0
            cursor.close()
            MitgliedAufgabe(mitgliedAufgabeId, mitgliedId, aufgabeId, erreicht)
        } else {
            cursor.close()
            null
        }
    }

    fun getMitgliedAufgabenByMitgliedId(mitgliedId: Int): List<MitgliedAufgabe> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED_AUFGABE}
            WHERE MITGLIED_AUFGABE_MITGLIED_ID = ?
        """
        val cursor = db.rawQuery(query, arrayOf(mitgliedId.toString()))

        val mitgliedAufgaben = mutableListOf<MitgliedAufgabe>()

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val mitgliedAufgabeId = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_AUFGABE_ID"))
                    val aufgabeId = cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_AUFGABE_AUFGABE_ID"))
                    val erreicht = cursor.getInt(cursor.getColumnIndexOrThrow("ERREICHT")) > 0

                    val mitgliedAufgabe = MitgliedAufgabe(mitgliedAufgabeId, mitgliedId, aufgabeId, erreicht)
                    mitgliedAufgaben.add(mitgliedAufgabe)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()

        Log.d("getMitgliedAufgabenByMitgliedId", "Mitglied ID: $mitgliedId, Aufgaben: $mitgliedAufgaben")
        return mitgliedAufgaben
    }
}
