package com.example.myswimsmartdb.db.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Anwesenheit(
    val anwesenheitId: Int,
    val mitgliedId: Int,
    val trainingId: Int,
    val trainingDatum: String, // Placeholder, adjust if necessary
    val bemerkung: String, // Placeholder, adjust if necessary
    val anwesend: Boolean
)
