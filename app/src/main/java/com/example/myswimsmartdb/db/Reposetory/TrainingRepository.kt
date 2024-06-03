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
        while (cursor.moveToNext()) {
            val trainingId = cursor.getInt(cursor.getColumnIndexOrThrow("TRAINING_ID"))
            val datum = cursor.getString(cursor.getColumnIndexOrThrow("TRAINING_DATUM"))
            val bemerkung = cursor.getString(cursor.getColumnIndexOrThrow("TRAINING_BEMERKUNG"))

            val training = Training(trainingId, datum, bemerkung)
            trainings.add(training)
        }
        cursor.close()
        return trainings
    }

    fun deleteTraining(trainingId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_TRAINING, "TRAINING_ID = ?", arrayOf(trainingId.toString()))
    }

    fun deleteAnwesenheitByTrainingId(trainingId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_ANWESENHEIT, "ANWESENHEIT_TRAINING_ID = ?", arrayOf(trainingId.toString()))
    }
}
