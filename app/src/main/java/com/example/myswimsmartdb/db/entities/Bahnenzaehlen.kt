package com.example.myswimsmartdb.db.entities

class Bahnenzaehlen(
    val id: Int,
    val mitgliedId: Int,
    val vorname: String,
    val nachname: String,
    val datumString: String,
    var bahnen: Int = 0,
    var bahnlaenge: Int,
    val zeitMode: String,
    var zeit: Long = when (zeitMode) {
        "15 Minuten" -> 15 * 60 * 1000L
        "20 Minuten" -> 20 * 60 * 1000L
        "30 Minuten" -> 30 * 60 * 1000L
        else -> 0L
    },
    var running: Boolean = false
) {
    fun start() {
        running = true
    }

    fun stop() {
        running = false
    }

    fun reset() {
        running = false
        zeit = when (zeitMode) {
            "15 Minuten" -> 15 * 60 * 1000L
            "20 Minuten" -> 20 * 60 * 1000L
            "30 Minuten" -> 30 * 60 * 1000L
            else -> 0L
        }
        bahnen = 0 // Reset the laps to 0
    }

    fun addBahnen() {
        bahnen++
    }

    fun getTotalMeters(): Int {
        return bahnen * bahnlaenge
    }
}
