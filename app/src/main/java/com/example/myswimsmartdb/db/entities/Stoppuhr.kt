package com.example.myswimsmartdb.db.entities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Stoppuhr(
    val id: Int,
    val mitgliedId: Int,
    val vorname: String,
    val nachname: String,
    var zeit: Long = 0L,
    var running: Boolean = false,
    var bemerkung: String = "",
    var schwimmarten: List<String> = listOf("Brust", "Rücken", "Kraul", "Freistil", "Lagen", "Delfin"),
    var schwimmart: String = "", // Neues Feld
    var laenge: String = "", // Neues Feld
    var datum: Date = Date()
) {
    val datumString: String
        get() {
            val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            return format.format(datum)
        }

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
