package com.example.myswimsmartdb.db.entities

data class MitgliedAufgabe(
    val mitgliedAufgabeId: Int,
    val mitgliedId: Int,
    val aufgabeId: Int,
    var erreicht: Boolean
)
