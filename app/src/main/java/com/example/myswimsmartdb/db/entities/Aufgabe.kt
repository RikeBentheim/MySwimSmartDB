package com.example.myswimsmartdb.db.entities

import android.os.Parcel
import android.os.Parcelable

data class Aufgabe(
    val id: Int,
    var erledigt: Boolean,
    val aufgabe: String,
    val beschreibung: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByte(if (erledigt) 1 else 0)
        parcel.writeString(aufgabe)
        parcel.writeString(beschreibung)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Aufgabe> {
        override fun createFromParcel(parcel: Parcel): Aufgabe {
            return Aufgabe(parcel)
        }

        override fun newArray(size: Int): Array<Aufgabe?> {
            return arrayOfNulls(size)
        }
    }
}
