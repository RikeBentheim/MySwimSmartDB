package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.AufgabeRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.MitgliedAufgabe

@Composable
fun MitgliedAufgabeTab(taskId: Int, kursId: Int) {
    val context = LocalContext.current
    val mitgliedRepository = MitgliedRepository(context)
    val aufgabeRepository = AufgabeRepository(context)
    var mitglieder by remember { mutableStateOf(listOf<Mitglied>()) }
    var mitgliedAufgaben by remember { mutableStateOf(listOf<MitgliedAufgabe>()) }
    val changes = remember { mutableStateMapOf<Int, Boolean>() }
    var aufgabeText by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
        mitgliedAufgaben = mitgliedRepository.getMitgliedAufgabenByAufgabeId(taskId)
        mitgliedAufgaben.forEach { aufgabe ->
            changes[aufgabe.mitgliedId] = aufgabe.erreicht
        }
        val aufgabe = aufgabeRepository.getAufgabeById(taskId)
        aufgabeText = aufgabe?.aufgabe ?: "Aufgabe nicht gefunden"
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Aufgabe: $aufgabeText",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(mitglieder) { mitglied ->
                val isChecked = changes[mitglied.id] ?: false

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${mitglied.vorname} ${mitglied.nachname}", style = MaterialTheme.typography.bodyLarge)
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            changes[mitglied.id] = checked
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                changes.forEach { (mitgliedId, erreicht) ->
                    mitgliedRepository.updateMitgliedAufgabeErreicht(mitgliedId, taskId, erreicht)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Text(text = "Änderungen speichern")
        }
    }
}
