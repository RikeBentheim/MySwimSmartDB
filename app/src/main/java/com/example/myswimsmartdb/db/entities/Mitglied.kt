package com.example.myswimsmartdb.db.entities

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Mitglied(
    val id: Int,
    val vorname: String,
    val nachname: String,
    val geburtsdatumString: String, // Das Datum als String speichern
    val telefon: String,
    val kursId: Int,
    var anwesenheiten: List<Anwesenheit> = emptyList(),
    var aufgaben: List<Aufgabe> = emptyList(),
    var bahnenzaehlen: List<Bahnenzaehlen> = emptyList(),
    var stoppuhr: List<Stoppuhr> = emptyList()
) : Parcelable {

    var geburtsdatum: String = geburtsdatumString
        set(value) {
            field = value
            geburtsdatumAsDate = stringToDate(value)
        }

    var geburtsdatumAsDate: Date? = stringToDate(geburtsdatum)
        get() = stringToDate(geburtsdatum)
        set(value) {
            field = value
            geburtsdatum = dateToString(value)
        }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Funktion zur Konvertierung von Date zu String
        private fun dateToString(date: Date?): String {
            return date?.let { dateFormat.format(it) } ?: ""
        }

        // Funktion zur Konvertierung von String zu Date
        private fun stringToDate(dateString: String): Date? {
            return try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }

        @JvmField
        val CREATOR: Parcelable.Creator<Mitglied> = object : Parcelable.Creator<Mitglied> {
            override fun createFromParcel(parcel: Parcel): Mitglied {
                return Mitglied(parcel)
            }

            override fun newArray(size: Int): Array<Mitglied?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        mutableListOf<Anwesenheit>().apply { parcel.readList(this, Anwesenheit::class.java.classLoader) },
        mutableListOf<Aufgabe>().apply { parcel.readList(this, Aufgabe::class.java.classLoader) },
        mutableListOf<Bahnenzaehlen>().apply { parcel.readList(this, Bahnenzaehlen::class.java.classLoader) },
        mutableListOf<Stoppuhr>().apply { parcel.readList(this, Stoppuhr::class.java.classLoader) }
    ) {
        geburtsdatum = parcel.readString() ?: ""
        geburtsdatumAsDate = stringToDate(geburtsdatum)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(vorname)
        parcel.writeString(nachname)
        parcel.writeString(geburtsdatum)
        parcel.writeString(telefon)
        parcel.writeInt(kursId)
        parcel.writeList(anwesenheiten)
        parcel.writeList(aufgaben)
        parcel.writeList(bahnenzaehlen)
        parcel.writeList(stoppuhr)
        parcel.writeString(geburtsdatum)
    }

    override fun describeContents(): Int {
        return 0
    }
}
