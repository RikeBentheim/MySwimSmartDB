package com.example.myswimsmartdb.db.Reposetory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.myswimsmartdb.db.DatabaseHelper
import com.example.myswimsmartdb.db.entities.Baderegel
import com.example.myswimsmartdb.R

class BaderegelRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertBaderegel(baderegel: Baderegel): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IMAGE_RES_ID", baderegel.imageResId)
            put("DESCRIPTION", baderegel.description)
        }
        val baderegelId = db.insert(DatabaseHelper.TABLE_BADEREGELN, null, values)

        // Insert into TABLE_BADEREGEL_LEVEL
        baderegel.levels.forEach { levelId ->
            val levelValues = ContentValues().apply {
                put("BADEREGEL_ID", baderegelId)
                put("LEVEL_ID", levelId)
            }
            db.insert(DatabaseHelper.TABLE_BADEREGEL_LEVEL, null, levelValues)
        }

        return baderegelId
    }

    fun getAllBaderegeln(): List<Baderegel> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_BADEREGELN,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val baderegeln = mutableListOf<Baderegel>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("ID"))
                val imageResId = getInt(getColumnIndexOrThrow("IMAGE_RES_ID"))
                val description = getString(getColumnIndexOrThrow("DESCRIPTION"))
                val levels = getLevelsForBaderegel(id)
                baderegeln.add(Baderegel(id, imageResId, description, levels))
            }
        }
        cursor.close()
        return baderegeln
    }

    private fun getLevelsForBaderegel(baderegelId: Int): List<Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_BADEREGEL_LEVEL,
            arrayOf("LEVEL_ID"),
            "BADEREGEL_ID = ?",
            arrayOf(baderegelId.toString()),
            null,
            null,
            null
        )

        val levels = mutableListOf<Int>()
        with(cursor) {
            while (moveToNext()) {
                levels.add(getInt(getColumnIndexOrThrow("LEVEL_ID")))
            }
        }
        cursor.close()
        return levels
    }

    // New method to check if the table is empty
    fun isTableEmpty(): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_BADEREGELN}", null)
        var isEmpty = true
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0
        }
        cursor.close()
        return isEmpty
    }
    fun populateDefaultData() {
        val defaultBaderegeln = listOf(
            Baderegel(0, R.drawable.baderegeln_neu_gutdrauf, "ich gehe nur baden, wenn ich mich gut fühle.", listOf(1,2)),
            Baderegel(0, R.drawable.baderegeln_neu_nichtschubsen, "Ich nehme Rücksicht! Ich renne nicht, schubse nicht und drücke niemanden unter Wasser.", listOf(1,2)),
            Baderegel(0, R.drawable.baderegeln_neu_zuzweit_kopie, "Ich sage Bescheid, wenn ich ins Wasser gehe.", listOf(1, 2)),
            Baderegel(0, R.drawable.baderegeln_neuerlaubt, "Ich gehe nur baden, wenn mir bei Problemen jemand helfen kann.", listOf(1, 2)),
            Baderegel(0, R.drawable.boot, "Beim Kentern schwimme zunächst weg. Versuche dich dann am Boot festzuhalten. Bist du unterm Boot, tauche und schwimme seitwärts davon.", listOf(3)),
            Baderegel(0, R.drawable.br_erfrischung, "Ich kühle mich ab, bevor ich ins Wasser gehe.", listOf(1, 2)),
            Baderegel(0, R.drawable.br_essen, "Ich gehe weder hungrig noch direkt nach dem Essen ins Wasser", listOf(1, 2)),
            Baderegel(0, R.drawable.br_gewitter, "Wenn ich draußen bade, gehe ich sofort aus dem Wasser, wenn es blitzt, " +
                    "donnert oder stark regnet. Baden bei Gewitter ist lebensgefährlich.", listOf(1, 2)),
            Baderegel(0, R.drawable.br_helfen, "Wenn ich Probleme im Wasser habe, dann rufe ich laut um Hilfe und winke mit den Armen. Ich helfe Anderen, wenn sie im Wasser Probleme " +
                    "haben. Ich rufe nie „Hilfe“, wenn alles in Ordnung ist.", listOf(1, 2)),
            Baderegel(0, R.drawable.br_krokodil, "Schwimmflügel, Schwimmtiere und Luftmatratze sind " +
                    "nicht sicher und schützen mich nicht vor dem Ertrinken.", listOf(1, 2)),
            Baderegel(0, R.drawable.br_springer, "Ich gehe nur da baden, wo es erlaubt ist. Ich springe nur da " +
                    "ins Wasser, wo das Wasser tief und frei ist.", listOf(1, 2)),
            Baderegel(0, R.drawable.er_arzt, "Rufe nach der Rettung einen Notarzt. Eine Unterkühlung kann lebensbedrohlich sein.", listOf( 2)),
            Baderegel(0, R.drawable.er_eisdicke, "Betritt einen See erst, wenn das Eis 15 Zentimeter dick ist. Ein fließendes Gewässer erst, " +
                    "wenn das Eis 20 Zentimeter dick ist.", listOf( 2)),
            Baderegel(0, R.drawable.er_ast, "Hilf anderen, wenn sie Hilfe brauchen.", listOf( 2)),
            Baderegel(0, R.drawable.er_flachaufseis, "Lege dich flach aufs Eis und bewege dich vorsichtig auf dem gleichen Weg zurück Richtung Ufer, " +
                    "wenn du einzubrechen drohst.", listOf( 2)),
            Baderegel(0, R.drawable.er_kalender, "Gehe ncht gleich an den ersten kalten Tagen aufs" +
                    " Eis!", listOf( 2)),
            Baderegel(0, R.drawable.er_paar, "Gehe nie allein aufs Eis!", listOf( 2)),
            Baderegel(0, R.drawable.er_rufehilfe, "Rufe nie um Hilfe, wenn Du nicht wirklich in Gefahr bist, aber hilf " +
                    "anderen, wenn sie Hilfe brauchen.", listOf( 2)),
            Baderegel(0, R.drawable.er_schnellanland, "Verlasse das Eis sofort, wenn es knistert und knackt!", listOf(2)),
            Baderegel(0, R.drawable.er_radio, "Achte auf Warnungen im Radio und in der Zeitung.", listOf( 2)),
            Baderegel(0, R.drawable.er_schlitten, "Um das Gewicht zu verteilen, rette andere mit einem Brett, einer Leiter oder " +
                    "einem umgedrehten Schlitten.", listOf(2)),
            Baderegel(0, R.drawable.er_telefon, "Erkundige dich beim zuständigen Amt, ob das " +
                    "Eis schon trägt!", listOf( 2)),
            Baderegel(0, R.drawable.er_waermflasche, "Wärme den Geretteten mit Decken und " +
                    "trockenen Kleidern wieder auf.", listOf( 2)),
            Baderegel(0, R.drawable.erschoepfung, "Erschöpft? Ruhe dich in Bauch- oder Rückenlage aus. Froschlage:Lege dich entspannt auf den Bauch. Lege das Gesicht ins Wasser. " +
                    "Hebe nur zum Einatmen den Mund kurz aus dem Wasser. Atme ruhig und langsam ins Wasser aus. Lege dich flach auf den Rücken. Tauche den Kopf bis zu den Ohren ein. Sorge mit den Händen und Beinen für das Gleichgewicht. " +
                    "Liege möglichst ruhig und erhole dich. Falls möglich: Mache andere auf dich aufmerksam. ", listOf( 2,3)),
            Baderegel(0, R.drawable.seestern, "Erschöpft? Ruhe dich in Bauch- oder Rückenlage aus. Rückenlage ", listOf( 2,3)),
            Baderegel(0, R.drawable.krampf_wade, "Ruhig bleiben und den Muskel dehnen. Wadenkrampf:Fasse die Fußspitze und ziehe sie zum Körper hin.Strecke das Bein (freie Hand drückt oberhalb des Knies).", listOf( 2,3)),
            Baderegel(0, R.drawable.oberschenkel, "Ruhig bleiben und den Muskel dehnen. Oberschenkelkrampf:Fasse den Unterschenkel am Fußgelenk. Ziehe die Ferse Richtung Gesäß.", listOf( 2,3)),
            Baderegel(0, R.drawable.fingerkrampf, "Fingerkrampf: Finger wiederholt zur Faust schließen und ruckartig strecken. ", listOf(2,3)),
            Baderegel(0, R.drawable.unterarmkrampf_, "Unterarmkrampf:Handflächen aneinander legen und die Hände so drehen, dass die Fingerspitzen zur Brust gerichtet sind.", listOf(2,3))
        )

        defaultBaderegeln.forEach {
            insertBaderegel(it)
        }
    }
}
