package com.example.myswimsmartdb.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "deine_datenbank_name.db"
        private const val DATABASE_VERSION = 4 // Increment the version to 4 for new columns

        public const val TABLE_LEVEL = "TABLE_LEVEL"
        public const val TABLE_KURS = "TABLE_KURS"
        public const val TABLE_MITGLIED = "TABLE_MITGLIED"
        public const val TABLE_AUFGABE = "TABLE_AUFGABE"
        public const val TABLE_ANWESENHEIT = "TABLE_ANWESENHEIT"
        public const val TABLE_TRAINING = "TABLE_TRAINING"
        public const val TABLE_KURS_TRAINING = "TABLE_KURS_TRAINING"
        public const val TABLE_MITGLIED_AUFGABE = "TABLE_MITGLIED_AUFGABE"
        public const val TABLE_LEVEL_AUFGABE = "TABLE_LEVEL_AUFGABE"
        public const val TABLE_STOPPUHR = "TABLE_STOPPUHR"
        public const val TABLE_BAHNENZAEHLEN = "TABLE_BAHNENZAEHLEN"

        // SQL statements to create tables
        public const val CREATE_TABLE_LEVEL = """
            CREATE TABLE $TABLE_LEVEL (
                LEVEL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                LEVEL_NAME TEXT
            )
        """

        public const val CREATE_TABLE_KURS = """
            CREATE TABLE $TABLE_KURS (
                KURS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                KURS_LEVEL_ID INTEGER,
                KURS_NAME TEXT,
                KURS_AKTIV INTEGER
            )
        """

        public const val CREATE_TABLE_MITGLIED = """
            CREATE TABLE $TABLE_MITGLIED (
                MITGLIED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                MITGLIED_VORNAME TEXT,
                MITGLIED_NACHNAME TEXT,
                MITGLIED_GEBURTSDATUM DATE,
                MITGLIED_TELEFON TEXT,
                MITGLIED_KURS_ID INTEGER
            )
        """

        public const val CREATE_TABLE_AUFGABE = """
            CREATE TABLE $TABLE_AUFGABE (
                AUFGABE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                AUFGABE_ERLEDIGT INTEGER,
                AUFGABE_TEXT TEXT,
                AUFGABE_BESCHREIBUNG TEXT
            )
        """

        public const val CREATE_TABLE_ANWESENHEIT = """
            CREATE TABLE $TABLE_ANWESENHEIT (
                ANWESENHEIT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                ANWESENHEIT_MITGLIED_ID INTEGER,
                ANWESENHEIT_TRAINING_ID INTEGER,
                ANWESENHEIT_ANWESEND INTEGER
            )
        """

        public const val CREATE_TABLE_TRAINING = """
            CREATE TABLE $TABLE_TRAINING (
                TRAINING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                TRAINING_DATUM DATE,
                TRAINING_BEMERKUNG TEXT
            )
        """

        public const val CREATE_TABLE_KURS_TRAINING = """
            CREATE TABLE $TABLE_KURS_TRAINING (
                KURS_TRAINING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                KURS_TRAINING_KURS_ID INTEGER,
                KURS_TRAINING_TRAINING_ID INTEGER
            )
        """

        // Updated create table statement for TABLE_MITGLIED_AUFGABE
        public const val CREATE_TABLE_MITGLIED_AUFGABE = """
            CREATE TABLE $TABLE_MITGLIED_AUFGABE (
                MITGLIED_AUFGABE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                MITGLIED_AUFGABE_MITGLIED_ID INTEGER,
                MITGLIED_AUFGABE_AUFGABE_ID INTEGER,
                ERREICHT INTEGER DEFAULT 0  -- Add the new column
            )
        """

        public const val CREATE_TABLE_LEVEL_AUFGABE = """
            CREATE TABLE $TABLE_LEVEL_AUFGABE (
                LEVEL_AUFGABE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                LEVEL_AUFGABE_AUFGABE_ID INTEGER,
                LEVEL_AUFGABE_LEVEL_ID INTEGER,
                FOREIGN KEY(LEVEL_AUFGABE_AUFGABE_ID) REFERENCES $TABLE_AUFGABE(AUFGABE_ID),
                FOREIGN KEY(LEVEL_AUFGABE_LEVEL_ID) REFERENCES $TABLE_LEVEL(LEVEL_ID)
            )
        """

        public const val CREATE_TABLE_STOPPUHR = """
            CREATE TABLE $TABLE_STOPPUHR (
                STOPPUHR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                MITGLIED_ID INTEGER,
                VORNAME TEXT,
                NACHNAME TEXT,
                ZEIT LONG,
                RUNNING INTEGER,
                DATUMSTRING TEXT
            )
        """

        public const val CREATE_TABLE_BAHNENZAEHLEN = """
            CREATE TABLE $TABLE_BAHNENZAEHLEN (
                BAHNENZAEHLEN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                MITGLIED_ID INTEGER,
                VORNAME TEXT,
                NACHNAME TEXT,
                BAHNEN INTEGER,
                BAHNLAENGE INTEGER,
                ZEITMODE TEXT,
                ZEIT LONG,
                RUNNING INTEGER,
                DATUMSTRING TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_LEVEL)
        db?.execSQL(CREATE_TABLE_KURS)
        db?.execSQL(CREATE_TABLE_MITGLIED)
        db?.execSQL(CREATE_TABLE_AUFGABE)
        db?.execSQL(CREATE_TABLE_ANWESENHEIT)
        db?.execSQL(CREATE_TABLE_TRAINING)
        db?.execSQL(CREATE_TABLE_KURS_TRAINING)
        db?.execSQL(CREATE_TABLE_MITGLIED_AUFGABE)
        db?.execSQL(CREATE_TABLE_LEVEL_AUFGABE)
        db?.execSQL(CREATE_TABLE_STOPPUHR)
        db?.execSQL(CREATE_TABLE_BAHNENZAEHLEN)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_MITGLIED_AUFGABE ADD COLUMN ERREICHT INTEGER DEFAULT 0")
        }
        if (oldVersion < 3) {
            db?.execSQL(CREATE_TABLE_STOPPUHR)
            db?.execSQL(CREATE_TABLE_BAHNENZAEHLEN)
        }
        if (oldVersion < 4) {
            db?.execSQL("ALTER TABLE $TABLE_STOPPUHR ADD COLUMN DATUMSTRING TEXT")
            db?.execSQL("ALTER TABLE $TABLE_BAHNENZAEHLEN ADD COLUMN DATUMSTRING TEXT")
        }

    }
}
