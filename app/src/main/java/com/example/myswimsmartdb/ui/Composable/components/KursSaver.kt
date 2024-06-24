package com.example.myswimsmartdb.ui.Composable.components

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Training
import androidx.compose.runtime.saveable.listSaver
import com.example.myswimsmartdb.db.entities.Aufgabe

val KursSaver = listSaver<Kurs?, Any>(
    save = {
        if (it == null) emptyList() else listOf(it.id, it.name, it.levelId, it.levelName, it.mitglieder, it.trainings, it.aufgaben)
    },
    restore = {
        if (it.isEmpty()) null else Kurs(
            id = it[0] as Int,
            name = it[1] as String,
            levelId = it[2] as Int,
            levelName = it[3] as String,
            mitglieder = it[4] as List<Mitglied>,
            trainings = it[5] as List<Training>,
            aufgaben = it[6] as List<Aufgabe>
        )
    }
)
