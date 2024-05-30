package com.example.myswimsmartdb.db

import android.content.ContentValues
import android.content.Context
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Level

class LevelRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    init {
        // Initialisiere die Datenbank mit den Standard-Levels und Aufgaben, falls noch keine existieren
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_LEVEL}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        if (count == 0) {
            insertDefaultLevelsAndAufgaben()
        }
    }

    private fun insertDefaultLevelsAndAufgaben() {
        val aufgabenBronze = listOf(
            Aufgabe(0, false, "Theoretische Prüfungsleistungen", "Die theoretische Prüfung umfasst die Kenntnis von Baderegeln"),
            Aufgabe(0, false, "Sprung kopfwärts vom Beckenrand", "Sprung kopfwärts vom Beckenrand"),
            Aufgabe(0, false, "15 Minuten Schwimmen", "In dieser Zeit sind mindestens 200 m zurückzulegen, davon 150 m in Bauch- oder Rückenlage in einer erkennbaren Schwimmart und 50 m in der anderen Körperlage"),
            Aufgabe(0, false, "Tieftauchen", "einmal ca. 2 m Tieftauchen von der Wasseroberfläche mit Heraufholen eines Gegenstandes (z. B.: kleiner Tauchring)")
        )

        val aufgabenSilber = listOf(
            Aufgabe(0, false, "Theoretische Prüfungsleistungen", "Die theoretische Prüfung umfasst die Kenntnis von Baderegeln und Verhalten zur Selbstrettung (z.B. Verhalten bei Erschöpfung, Lösen von Krämpfen)"),
            Aufgabe(0, false, "Sprung kopfwärts", "Sprung kopfwärts vom Beckenrand"),
            Aufgabe(0, false, "20 Minuten Schwimmen", "In dieser Zeit sind mindestens 400 m zurückzulegen, davon 300 m in Bauch- oder Rückenlage in einer erkennbaren Schwimmart und 100 m in der anderen Körperlage"),
            Aufgabe(0, false, "Zweimal Tieftauchen", "zweimal ca. 2 m Tieftauchen von der Wasseroberfläche mit Heraufholen eines Gegenstandes"),
            Aufgabe(0, false, "StreckeTauchen", "10 m Streckentauchen mit Abstoßen vom Beckenrand im Wasser"),
            Aufgabe(0, false, "Startblock Sprung", "Sprung aus 3 m Höhe oder zwei verschiedene Sprünge aus 1 m Höhe")
        )

        val aufgabenGold = listOf(
            Aufgabe(0, false, "Theoretische Prüfungsleistungen", "Die theoretische Prüfung umfasst die Kenntnisse von Baderegeln sowie von der Hilfe bei Bade-, Boots- und Eisunfällen"),
            Aufgabe(0, false, "30 Minuten Schwimmen", "In dieser Zeit sind mindestens 800 m zurückzulegen, davon 650 m in Bauch- oder Rückenlage und 150 m in der anderen Körperlage"),
            Aufgabe(0, false, "Dreimal Tieftauchen", "dreimal ca. 2 m Tieftauchen von der Wasseroberfläche mit Heraufholen je eines Gegenstandes innerhalb von 3 Minuten"),
            Aufgabe(0, false, "Streckentauchen aus der Schwimmlage", "10 m Streckentauchen aus der Schwimmlage ohne Abstoßen vom Beckenrand"),
            Aufgabe(0, false, "Sprung 3m", "Sprung aus 3m Höhe oder zwei verschiedene Sprünge aus 1 m Höhe"),
            Aufgabe(0, false, "Kraulschwimmen", "Startsprung und 25 m Kraulschwimmen"),
            Aufgabe(0, false, "Brustschwimmen", "Startsprung und 50 m Brustschwimmen in höchstens 1:15 Minuten"),
            Aufgabe(0, false, "Rückenschwimmen", "50 m Rückenschwimmen mit Grätschschwung ohne Armtätigkeit oder Rückenkraulschwimmen"),
            Aufgabe(0, false, "Transportschwimmen", "50 m Transportschwimmen: Schieben oder Ziehen")
        )

        insertLevelWithAufgaben("Bronze", aufgabenBronze)
        insertLevelWithAufgaben("Silber", aufgabenSilber)
        insertLevelWithAufgaben("Gold", aufgabenGold)
    }

    private fun insertLevelWithAufgaben(levelName: String, aufgaben: List<Aufgabe>) {
        val db = dbHelper.writableDatabase
        val levelValues = ContentValues().apply {
            put("LEVEL_NAME", levelName)
        }
        val levelId = db.insert(DatabaseHelper.TABLE_LEVEL, null, levelValues)

        if (levelId != -1L) {
            for (aufgabe in aufgaben) {
                val aufgabeValues = ContentValues().apply {
                    put("AUFGABE_ERLEDIGT", if (aufgabe.erledigt) 1 else 0)
                    put("AUFGABE_TEXT", aufgabe.aufgabe)
                    put("AUFGABE_BESCHREIBUNG", aufgabe.beschreibung)
                }
                val aufgabeId = db.insert(DatabaseHelper.TABLE_AUFGABE, null, aufgabeValues)

                if (aufgabeId != -1L) {
                    val levelAufgabeValues = ContentValues().apply {
                        put("LEVEL_AUFGABE_LEVEL_ID", levelId)
                        put("LEVEL_AUFGABE_AUFGABE_ID", aufgabeId)
                    }
                    db.insert(DatabaseHelper.TABLE_LEVEL_AUFGABE, null, levelAufgabeValues)
                }
            }
        }
    }

    fun getAllLevelsWithAufgaben(): List<Level> {
        val db = dbHelper.readableDatabase
        val levelQuery = "SELECT * FROM ${DatabaseHelper.TABLE_LEVEL}"
        val levelCursor = db.rawQuery(levelQuery, null)

        val levels = mutableListOf<Level>()

        while (levelCursor.moveToNext()) {
            val levelId = levelCursor.getInt(levelCursor.getColumnIndexOrThrow("LEVEL_ID"))
            val levelName = levelCursor.getString(levelCursor.getColumnIndexOrThrow("LEVEL_NAME"))

            val aufgabenQuery = """
                SELECT a.*
                FROM ${DatabaseHelper.TABLE_AUFGABE} a
                INNER JOIN ${DatabaseHelper.TABLE_LEVEL_AUFGABE} la
                ON a.AUFGABE_ID = la.LEVEL_AUFGABE_AUFGABE_ID
                WHERE la.LEVEL_AUFGABE_LEVEL_ID = ?
            """
            val aufgabenCursor = db.rawQuery(aufgabenQuery, arrayOf(levelId.toString()))

            val aufgaben = mutableListOf<Aufgabe>()
            while (aufgabenCursor.moveToNext()) {
                val aufgabeId = aufgabenCursor.getInt(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_ID"))
                val erledigt = aufgabenCursor.getInt(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_ERLEDIGT")) > 0
                val text = aufgabenCursor.getString(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_TEXT"))
                val beschreibung = aufgabenCursor.getString(aufgabenCursor.getColumnIndexOrThrow("AUFGABE_BESCHREIBUNG"))
                aufgaben.add(Aufgabe(aufgabeId, erledigt, text, beschreibung))
            }
            aufgabenCursor.close()

            levels.add(Level(levelId, levelName, aufgaben))
        }
        levelCursor.close()

        return levels
    }

    fun getAllLevels(): List<Level> {
        val db = dbHelper.readableDatabase
        val levelQuery = "SELECT * FROM ${DatabaseHelper.TABLE_LEVEL}"
        val levelCursor = db.rawQuery(levelQuery, null)

        val levels = mutableListOf<Level>()
        while (levelCursor.moveToNext()) {
            val levelId = levelCursor.getInt(levelCursor.getColumnIndexOrThrow("LEVEL_ID"))
            val levelName = levelCursor.getString(levelCursor.getColumnIndexOrThrow("LEVEL_NAME"))
            levels.add(Level(levelId, levelName, emptyList()))
        }
        levelCursor.close()

        return levels
    }

}
