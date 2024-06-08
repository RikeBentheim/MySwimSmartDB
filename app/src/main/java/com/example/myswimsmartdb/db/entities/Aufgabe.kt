package com.example.myswimsmartdb.db.entities

data class Aufgabe(
    val id: Int,
    var erledigt: Boolean,
    val aufgabe: String,
    var beschreibung: String
)