package com.example.myswimsmartdb.db.entities

data class Baderegel(
    val id: Int,
    val imageResId: Int,
    val description: String,
    val levels: List<Int> // List of level IDs (Bronze, Silber, Gold)
)

