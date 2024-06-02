package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import com.example.myswimsmartdb.db.entities.Training

class TrainingRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertTraining(training: Training, kursId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("TRAINING_DATUM", training.datumString)
            put("TRAINING_BEMERKUNG", training.bemerkung)
        }
        val trainingId = db.insert(DatabaseHelper.TABLE_TRAINING, null, values)
        if (trainingId != -1L) {
            val kursTrainingValues = ContentValues().apply {
                put("KURS_TRAINING_KURS_ID", kursId)
                put("KURS_TRAINING_TRAINING_ID", trainingId)
            }
            db.insert(DatabaseHelper.TABLE_KURS_TRAINING, null, kursTrainingValues)
        }
        return trainingId
    }

    fun insertAnwesenheit(mitgliedId: Int, trainingId: Long) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("ANWESENHEIT_MITGLIED_ID", mitgliedId)
            put("ANWESENHEIT_TRAINING_ID", trainingId)
            put("ANWESENHEIT_ANWESEND", 0)
        }
        db.insert(DatabaseHelper.TABLE_ANWESENHEIT, null, values)
    }
    fun getTrainingsByKursId(kursId: Int): List<Training> {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT t.* FROM ${DatabaseHelper.TABLE_TRAINING} t
        INNER JOIN ${DatabaseHelper.TABLE_KURS_TRAINING} kt
        ON t.TRAINING_ID = kt.KURS_TRAINING_TRAINING_ID
        WHERE kt.KURS_TRAINING_KURS_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(kursId.toString()))
        val trainings = mutableListOf<Training>()
        with(cursor) {
            while (moveToNext()) {
                val trainingId = getInt(getColumnIndexOrThrow("TRAINING_ID"))
                val datum = getString(getColumnIndexOrThrow("TRAINING_DATUM"))
                val bemerkung = getString(getColumnIndexOrThrow("TRAINING_BEMERKUNG"))
                trainings.add(Training(trainingId, datum, bemerkung))
            }
        }
        cursor.close()
        return trainings
    }
}
