package com.example.myswimsmartdb.db.entities

data class Kurs(
    val id: Int,
    val name: String,
    val levelId: Int,
    val levelName: String,
    val mitglieder: List<Mitglied> = emptyList(),
    val trainings: List<Training> = emptyList(),
    val aufgaben: List<Aufgabe> = emptyList()
)