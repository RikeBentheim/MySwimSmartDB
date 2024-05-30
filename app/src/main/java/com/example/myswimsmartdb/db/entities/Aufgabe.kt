package com.example.myswimsmartdb.db.entities

data class Aufgabe(
    val id: Int,
    val erledigt: Boolean,
    val aufgabe: String,
    val beschreibung: String
)