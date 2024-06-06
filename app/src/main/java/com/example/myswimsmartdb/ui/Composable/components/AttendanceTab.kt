package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AttendanceTab(kursId: Int, trainingId: Int) {
    val context = LocalContext.current
    val kursRepository = remember { KursRepository(context) }
    val scope = rememberCoroutineScope()
    var mitglieder by remember { mutableStateOf(emptyList<Mitglied>()) }
    var anwesenheiten by remember { mutableStateOf(mapOf<Int, Boolean>()) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    // Mitglieder des Kurses und Anwesenheiten laden
    LaunchedEffect(kursId, trainingId) {
        mitglieder = kursRepository.getMitgliederForKurs(kursId)
        anwesenheiten = kursRepository.getAnwesenheitForTraining(trainingId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(mitglieder) { mitglied ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Text(text = "${mitglied.vorname} ${mitglied.nachname}", modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = anwesenheiten[mitglied.id] ?: false,
                        onCheckedChange = { checked ->
                            anwesenheiten = anwesenheiten.toMutableMap().apply {
                                put(mitglied.id, checked)
                            }
                            hasChanges = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hasChanges) {
            Button(
                onClick = {
                    // Änderungen in der Anwesenheit speichern
                    mitglieder.forEach { mitglied ->
                        kursRepository.updateAnwesenheit(trainingId, mitglied.id, anwesenheiten[mitglied.id] ?: false)
                    }
                    hasChanges = false
                    showSuccessMessage = true
                    scope.launch {
                        delay(3000)
                        showSuccessMessage = false
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Änderungen übernehmen")
            }
        }

        if (showSuccessMessage) {
            Text(
                text = "Änderungen wurden gespeichert",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
