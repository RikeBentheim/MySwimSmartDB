package com.example.myswimsmartdb.db.entities


import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

