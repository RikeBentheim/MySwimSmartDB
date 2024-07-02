package com.example.myswimsmartdb.db.entities

import android.os.Parcel
import android.os.Parcelable
import kotlinx.coroutines.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTime::class)
data class Stoppuhr(
    val id: Int,
    val mitgliedId: Int,
    val vorname: String,
    val nachname: String,
    var zeit: Long = 0L,
    var running: Boolean = false,
    var bemerkung: String = "",
    var schwimmarten: List<String> = listOf("Brust", "Rücken", "Kraul", "Freistil", "Lagen", "Delfin"),
    var schwimmart: String = "",
    var laenge: String = "",
    var datum: Date = Date()
) : Parcelable {

    private var job: Job? = null
    private var startTime = 0L
    private var elapsedTime = 0L
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val datumString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(datum)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(mitgliedId)
        parcel.writeString(vorname)
        parcel.writeString(nachname)
        parcel.writeLong(zeit)
        parcel.writeByte(if (running) 1 else 0)
        parcel.writeString(bemerkung)
        parcel.writeStringList(schwimmarten)
        parcel.writeString(schwimmart)
        parcel.writeString(laenge)
        parcel.writeLong(datum.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stoppuhr> {
        override fun createFromParcel(parcel: Parcel): Stoppuhr {
            return Stoppuhr(parcel)
        }

        override fun newArray(size: Int): Array<Stoppuhr?> {
            return arrayOfNulls(size)
        }
    }

    fun start() {
        if (running) return
        running = true
        startTime = System.currentTimeMillis() - elapsedTime
        job = coroutineScope.launch {
            while (running) {
                elapsedTime = System.currentTimeMillis() - startTime
                delay(10L)
            }
        }
    }

    fun stop() {
        if (!running) return
        running = false
        zeit = elapsedTime
        job?.cancel()
    }

    fun reset() {
        stop()
        elapsedTime = 0L
        zeit = 0L
    }

    fun getTime(): Duration = elapsedTime.toDuration(DurationUnit.MILLISECONDS)
}
