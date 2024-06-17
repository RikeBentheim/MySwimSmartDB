package com.example.myswimsmartdb.db.entities

class Stoppuhr(
    val id: Int,
    val mitgliedId: Int,
    val vorname: String,
    val nachname: String,
    val datumString: String,
    var zeit: Long = 0L,
    var running: Boolean = false,
    var bemerkung: String = "",
    var schwimmarten: List<String> = listOf("Brust", "Rücken", "Kraul", "Freistil", "Lagen", "Delfin")
) {

    fun start() {
        running = true
    }

    fun stop() {
        running = false
    }

    fun reset() {
        running = false
        zeit = 0L
    }

    fun addTime(time: Long) {
        zeit += time
    }

}
