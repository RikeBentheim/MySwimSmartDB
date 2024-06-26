package com.example.myswimsmartdb.db.Reposetory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.myswimsmartdb.db.DatabaseHelper
import com.example.myswimsmartdb.db.entities.*
import com.example.myswimsmartdb.db.Reposetory.StoppuhrRepository
import com.example.myswimsmartdb.db.Reposetory.BahnenzaehlenRepository
import com.example.myswimsmartdb.db.Reposetory.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class KursRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    suspend fun getAllKurseWithDetails(): List<Kurs> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val kursQuery = "SELECT * FROM ${DatabaseHelper.TABLE_KURS}"
        val kursCursor = db.rawQuery(kursQuery, null)

        val kurse = mutableListOf<Kurs>()

        while (kursCursor.moveToNext()) {
            val kursId = kursCursor.getInt(kursCursor.getColumnIndexOrThrow("KURS_ID"))
            val kursName = kursCursor.getString(kursCursor.getColumnIndexOrThrow("KURS_NAME"))
            val levelId = kursCursor.getInt(kursCursor.getColumnIndexOrThrow("KURS_LEVEL_ID"))

            // Fetch Level Name
            val levelQuery = "SELECT LEVEL_NAME FROM ${DatabaseHelper.TABLE_LEVEL} WHERE LEVEL_ID = ?"
            val levelCursor = db.rawQuery(levelQuery, arrayOf(levelId.toString()))
            var levelName = ""
            if (levelCursor.moveToFirst()) {
                levelName = levelCursor.getString(levelCursor.getColumnIndexOrThrow("LEVEL_NAME"))
            }
            levelCursor.close()

            // Fetch Mitglieder
            val mitgliederQuery = """
                SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED} 
                WHERE MITGLIED_KURS_ID = ?
            """
            val mitgliederCursor = db.rawQuery(mitgliederQuery, arrayOf(kursId.toString()))

            val mitglieder = mutableListOf<Mitglied>()
            while (mitgliederCursor.moveToNext()) {
                val mitgliedId = mitgliederCursor.getInt(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_ID"))
                val vorname = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_VORNAME"))
                val nachname = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_NACHNAME"))
                val geburtsdatum = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_GEBURTSDATUM"))
                val telefon = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_TELEFON"))

                val mitglied = Mitglied(mitgliedId, vorname, nachname, geburtsdatum, telefon, kursId)
                mitglieder.add(mitglied)
            }
            mitgliederCursor.close()

            // Fetch Trainings
            val trainingsQuery = """
                SELECT t.* FROM ${DatabaseHelper.TABLE_TRAINING} t
                INNER JOIN ${DatabaseHelper.TABLE_KURS_TRAINING} kt
                ON t.TRAINING_ID = kt.KURS_TRAINING_TRAINING_ID
                WHERE kt.KURS_TRAINING_KURS_ID = ?
            """
            val trainingsCursor = db.rawQuery(trainingsQuery, arrayOf(kursId.toString()))

            val trainings = mutableListOf<Training>()
            while (trainingsCursor.moveToNext()) {
                val trainingId = trainingsCursor.getInt(trainingsCursor.getColumnIndexOrThrow("TRAINING_ID"))
                val datum = trainingsCursor.getString(trainingsCursor.getColumnIndexOrThrow("TRAINING_DATUM"))
                val bemerkung = trainingsCursor.getString(trainingsCursor.getColumnIndexOrThrow("TRAINING_BEMERKUNG"))

                val training = Training(trainingId, datum, bemerkung)
                trainings.add(training)
            }
            trainingsCursor.close()

            // Fetch Aufgaben
            val aufgabenQuery = """
                SELECT a.* FROM ${DatabaseHelper.TABLE_AUFGABE} a
                INNER JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} la
                ON a.AUFGABE_ID = la.LEVEL_AUFGABE_AUFGABE_ID
                WHERE la.LEVEL_AUFGABE_LEVEL_ID = ?
            """
            val aufgabenCursor = db.rawQuery(aufgabenQuery, arrayOf(levelId.toString()))

            val aufgaben = mutableListOf<Aufgabe>()
            while (aufgabenCursor.moveToNext()) {
                val aufgabeId = aufgabenCursor.getInt(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_ID"))
                val erledigt = aufgabenCursor.getInt(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
                val aufgabe = aufgabenCursor.getString(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
                val beschreibung = aufgabenCursor.getString(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))

                aufgaben.add(Aufgabe(aufgabeId, erledigt, aufgabe, beschreibung))
            }
            aufgabenCursor.close()

            kurse.add(Kurs(kursId, kursName, levelId, levelName, mitglieder, trainings, aufgaben))
        }
        kursCursor.close()

        kurse
    }

    suspend fun insertKursWithDetails(kurs: Kurs): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val kursValues = ContentValues().apply {
            put("KURS_NAME", kurs.name)
            put("KURS_LEVEL_ID", kurs.levelId)
            put("KURS_AKTIV", 1) // Assuming 1 means active
        }
        val kursId = db.insert(DatabaseHelper.TABLE_KURS, null, kursValues)

        if (kursId != -1L) {
            for (mitglied in kurs.mitglieder) {
                val mitgliedValues = ContentValues().apply {
                    put("MITGLIED_VORNAME", mitglied.vorname)
                    put("MITGLIED_NACHNAME", mitglied.nachname)
                    put("MITGLIED_GEBURTSDATUM", mitglied.geburtsdatum)
                    put("MITGLIED_TELEFON", mitglied.telefon)
                    put("MITGLIED_KURS_ID", kursId)
                }
                db.insert(DatabaseHelper.TABLE_MITGLIED, null, mitgliedValues)
            }

            for (training in kurs.trainings) {
                val trainingValues = ContentValues().apply {
                    put("TRAINING_DATUM", training.datum)
                    put("TRAINING_BEMERKUNG", training.bemerkung)
                }
                val trainingId = db.insert(DatabaseHelper.TABLE_TRAINING, null, trainingValues)

                if (trainingId != -1L) {
                    val kursTrainingValues = ContentValues().apply {
                        put("KURS_TRAINING_KURS_ID", kursId)
                        put("KURS_TRAINING_TRAINING_ID", trainingId)
                    }
                    db.insert(DatabaseHelper.TABLE_KURS_TRAINING, null, kursTrainingValues)
                }
            }
        }
        kursId
    }

    suspend fun deleteKursWithDetails(kursId: Int) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        try {
            // Löschen der Anwesenheitsaufzeichnungen für jedes Training des Kurses
            val trainingIdsCursor = db.rawQuery(
                "SELECT TRAINING_ID FROM ${DatabaseHelper.TABLE_TRAINING} t " +
                        "INNER JOIN ${DatabaseHelper.TABLE_KURS_TRAINING} kt " +
                        "ON t.TRAINING_ID = kt.KURS_TRAINING_TRAINING_ID " +
                        "WHERE kt.KURS_TRAINING_KURS_ID = ?", arrayOf(kursId.toString())
            )

            while (trainingIdsCursor.moveToNext()) {
                val trainingId = trainingIdsCursor.getInt(trainingIdsCursor.getColumnIndexOrThrow("TRAINING_ID"))
                db.delete(DatabaseHelper.TABLE_ANWESENHEIT, "ANWESENHEIT_TRAINING_ID = ?", arrayOf(trainingId.toString()))
            }
            trainingIdsCursor.close()

            // Löschen der Trainings für den Kurs
            db.delete(
                DatabaseHelper.TABLE_TRAINING,
                "TRAINING_ID IN (SELECT TRAINING_ID FROM ${DatabaseHelper.TABLE_TRAINING} t " +
                        "INNER JOIN ${DatabaseHelper.TABLE_KURS_TRAINING} kt " +
                        "ON t.TRAINING_ID = kt.KURS_TRAINING_TRAINING_ID " +
                        "WHERE kt.KURS_TRAINING_KURS_ID = ?)",
                arrayOf(kursId.toString())
            )

            // Löschen der Kurs-Training-Verknüpfungen
            db.delete(DatabaseHelper.TABLE_KURS_TRAINING, "KURS_TRAINING_KURS_ID = ?", arrayOf(kursId.toString()))

            // Löschen der Mitglied-Aufgabe-Verknüpfungen für jedes Mitglied des Kurses
            val mitgliedIdsCursor = db.rawQuery(
                "SELECT MITGLIED_ID FROM ${DatabaseHelper.TABLE_MITGLIED} WHERE MITGLIED_KURS_ID = ?",
                arrayOf(kursId.toString())
            )

            while (mitgliedIdsCursor.moveToNext()) {
                val mitgliedId = mitgliedIdsCursor.getInt(mitgliedIdsCursor.getColumnIndexOrThrow("MITGLIED_ID"))
                // Delete Mitglied Aufgabe Verknüpfungen
                db.delete(DatabaseHelper.TABLE_MITGLIED_AUFGABE, "MITGLIED_AUFGABE_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

                // Delete all related data for each member
                deleteAllDataByMitgliedId(mitgliedId, context)
            }
            mitgliedIdsCursor.close()

            // Löschen der Mitglieder des Kurses
            db.delete(DatabaseHelper.TABLE_MITGLIED, "MITGLIED_KURS_ID = ?", arrayOf(kursId.toString()))

            // Schließlich, löschen des Kurses selbst
            db.delete(DatabaseHelper.TABLE_KURS, "KURS_ID = ?", arrayOf(kursId.toString()))

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private suspend fun deleteAllDataByMitgliedId(mitgliedId: Int, context: Context) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val trainingRepository = TrainingRepository(context)
        val stoppuhrRepository = StoppuhrRepository(context)
        val bahnenzaehlenRepository = BahnenzaehlenRepository(context)

        // Start a transaction
        db.beginTransaction()
        try {
            // Delete tasks related to the member
            deleteMitgliedAufgabenByMitgliedId(mitgliedId)

            // Delete attendance records related to the member
            db.delete(DatabaseHelper.TABLE_ANWESENHEIT, "ANWESENHEIT_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

            // Delete Bahnenzaehlen records related to the member
            db.delete(DatabaseHelper.TABLE_BAHNENZAEHLEN, "MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

            // Delete Stoppuhr records related to the member
            db.delete(DatabaseHelper.TABLE_STOPPUHR, "MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

            // Delete the member itself
            db.delete(DatabaseHelper.TABLE_MITGLIED, "MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))

            // Set transaction as successful
            db.setTransactionSuccessful()
        } finally {
            // End the transaction
            db.endTransaction()
        }
    }

    private suspend fun deleteMitgliedAufgabenByMitgliedId(mitgliedId: Int): Int = withContext(Dispatchers.IO) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_MITGLIED_AUFGABE, "MITGLIED_AUFGABE_MITGLIED_ID = ?", arrayOf(mitgliedId.toString()))
    }
    fun getMitgliederForKurs(kursId: Int): List<Mitglied> {
        val db = dbHelper.readableDatabase
        val mitglieder = mutableListOf<Mitglied>()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED} WHERE MITGLIED_KURS_ID = ?", arrayOf(kursId.toString()))

        while (cursor.moveToNext()) {
            val mitglied = Mitglied(
                cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_ID")),
                cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_VORNAME")),
                cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_NACHNAME")),
                cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_GEBURTSDATUM")),
                cursor.getString(cursor.getColumnIndexOrThrow("MITGLIED_TELEFON")),
                cursor.getInt(cursor.getColumnIndexOrThrow("MITGLIED_KURS_ID"))
            )
            mitglieder.add(mitglied)
        }

        cursor.close()
        return mitglieder
    }

    fun getAnwesenheitForTraining(trainingId: Int): Map<Int, Boolean> {
        val db = dbHelper.readableDatabase
        val anwesenheiten = mutableMapOf<Int, Boolean>()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_ANWESENHEIT} WHERE ANWESENHEIT_TRAINING_ID = ?", arrayOf(trainingId.toString()))

        while (cursor.moveToNext()) {
            val mitgliedId = cursor.getInt(cursor.getColumnIndexOrThrow("ANWESENHEIT_MITGLIED_ID"))
            val anwesend = cursor.getInt(cursor.getColumnIndexOrThrow("ANWESENHEIT_ANWESEND")) > 0
            anwesenheiten[mitgliedId] = anwesend
        }

        cursor.close()
        return anwesenheiten
    }

    fun updateAnwesenheit(trainingId: Int, mitgliedId: Int, anwesend: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("ANWESENHEIT_TRAINING_ID", trainingId)
            put("ANWESENHEIT_MITGLIED_ID", mitgliedId)
            put("ANWESENHEIT_ANWESEND", if (anwesend) 1 else 0)
        }
        db.insertWithOnConflict(DatabaseHelper.TABLE_ANWESENHEIT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getKursWithDetailsById(kursId: Int): Kurs? {
        val db = dbHelper.readableDatabase
        val kursQuery = "SELECT * FROM ${DatabaseHelper.TABLE_KURS} WHERE KURS_ID = ?"
        val kursCursor = db.rawQuery(kursQuery, arrayOf(kursId.toString()))

        var kurs: Kurs? = null

        if (kursCursor.moveToFirst()) {
            val kursName = kursCursor.getString(kursCursor.getColumnIndexOrThrow("KURS_NAME"))
            val levelId = kursCursor.getInt(kursCursor.getColumnIndexOrThrow("KURS_LEVEL_ID"))

            // Fetch Level Name
            val levelQuery = "SELECT LEVEL_NAME FROM ${DatabaseHelper.TABLE_LEVEL} WHERE LEVEL_ID = ?"
            val levelCursor = db.rawQuery(levelQuery, arrayOf(levelId.toString()))
            var levelName = ""
            if (levelCursor.moveToFirst()) {
                levelName = levelCursor.getString(levelCursor.getColumnIndexOrThrow("LEVEL_NAME"))
            }
            levelCursor.close()

            // Fetch Mitglieder
            val mitgliederQuery = """
            SELECT * FROM ${DatabaseHelper.TABLE_MITGLIED} 
            WHERE MITGLIED_KURS_ID = ?
        """
            val mitgliederCursor = db.rawQuery(mitgliederQuery, arrayOf(kursId.toString()))

            val mitglieder = mutableListOf<Mitglied>()
            while (mitgliederCursor.moveToNext()) {
                val mitgliedId = mitgliederCursor.getInt(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_ID"))
                val vorname = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_VORNAME"))
                val nachname = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_NACHNAME"))
                val geburtsdatum = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_GEBURTSDATUM"))
                val telefon = mitgliederCursor.getString(mitgliederCursor.getColumnIndexOrThrow("MITGLIED_TELEFON"))

                val mitglied = Mitglied(mitgliedId, vorname, nachname, geburtsdatum, telefon, kursId)
                mitglieder.add(mitglied)
            }
            mitgliederCursor.close()

            // Fetch Trainings
            val trainingsQuery = """
            SELECT t.* FROM ${DatabaseHelper.TABLE_TRAINING} t
            INNER JOIN ${DatabaseHelper.TABLE_KURS_TRAINING} kt
            ON t.TRAINING_ID = kt.KURS_TRAINING_TRAINING_ID
            WHERE kt.KURS_TRAINING_KURS_ID = ?
        """
            val trainingsCursor = db.rawQuery(trainingsQuery, arrayOf(kursId.toString()))

            val trainings = mutableListOf<Training>()
            while (trainingsCursor.moveToNext()) {
                val trainingId = trainingsCursor.getInt(trainingsCursor.getColumnIndexOrThrow("TRAINING_ID"))
                val datum = trainingsCursor.getString(trainingsCursor.getColumnIndexOrThrow("TRAINING_DATUM"))
                val bemerkung = trainingsCursor.getString(trainingsCursor.getColumnIndexOrThrow("TRAINING_BEMERKUNG"))

                val training = Training(trainingId, datum, bemerkung)
                trainings.add(training)
            }
            trainingsCursor.close()

            // Fetch Aufgaben
            val aufgabenQuery = """
            SELECT a.* FROM ${DatabaseHelper.TABLE_AUFGABE} a
            INNER JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} la
            ON a.AUFGABE_ID = la.LEVEL_AUFGABE_AUFGABE_ID
            WHERE la.LEVEL_AUFGABE_LEVEL_ID = ?
        """
            val aufgabenCursor = db.rawQuery(aufgabenQuery, arrayOf(levelId.toString()))

            val aufgaben = mutableListOf<Aufgabe>()
            while (aufgabenCursor.moveToNext()) {
                val aufgabeId = aufgabenCursor.getInt(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_ID"))
                val erledigt = aufgabenCursor.getInt(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
                val aufgabe = aufgabenCursor.getString(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
                val beschreibung = aufgabenCursor.getString(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))

                aufgaben.add(Aufgabe(aufgabeId, erledigt, aufgabe, beschreibung))
            }
            aufgabenCursor.close()

            kurs = Kurs(kursId, kursName, levelId, levelName, mitglieder, trainings, aufgaben)
        }
        kursCursor.close()

        return kurs
    }
}
