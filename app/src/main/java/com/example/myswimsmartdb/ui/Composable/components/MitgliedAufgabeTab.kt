package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.MitgliedAufgabe

@Composable
fun MitgliedAufgabeTab(taskId: Int, kursId: Int) {
    val context = LocalContext.current
    val mitgliedRepository = MitgliedRepository(context)
    var mitglieder by remember { mutableStateOf(listOf<Mitglied>()) }
    var mitgliedAufgaben by remember { mutableStateOf(listOf<MitgliedAufgabe>()) }

    LaunchedEffect(kursId, taskId) {
        mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
        mitgliedAufgaben = mitgliedRepository.getMitgliedAufgabenByAufgabeId(taskId)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(mitglieder) { mitglied ->
            val mitgliedAufgabe = mitgliedAufgaben.find { it.mitgliedId == mitglied.id }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Text(text = "${mitglied.vorname} ${mitglied.nachname}", modifier = Modifier.weight(1f))
                Checkbox(
                    checked = mitgliedAufgabe?.erreicht ?: false,
                    onCheckedChange = { checked ->
                        mitgliedRepository.updateMitgliedAufgabeErreicht(mitglied.id, taskId, checked)
                    }
                )
            }
        }
    }
}
