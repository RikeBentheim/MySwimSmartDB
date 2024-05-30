package com.example.myswimsmartdb.db.entities

data class Level(
    val id: Int,
    val name: String,
    val aufgaben: List<Aufgabe>
)