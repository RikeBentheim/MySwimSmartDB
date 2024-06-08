package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Aufgabe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitgliedAufgabeTab(
    taskId: Int,
    kursId: Int,
    mitgliedRepository: MitgliedRepository,
    onBackToTasks: () -> Unit,
    navController: NavHostController
) {
    var mitglieder by remember { mutableStateOf<List<Mitglied>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var taskStatusMap by remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }
    var hasChanges by remember { mutableStateOf(false) }

    LaunchedEffect(kursId) {
        val mitgliederResult = mitgliedRepository.getFullMitgliederDetailsByKursId(kursId)
        val taskStatusMapResult = mitgliederResult.associate { it.id to (it.aufgaben.find { aufgabe -> aufgabe.id == taskId }?.erledigt ?: false) }
        mitglieder = mitgliederResult
        taskStatusMap = taskStatusMapResult
        isLoading = false
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mitglieder) { mitglied ->
                    MitgliedRow(mitglied, taskStatusMap[mitglied.id] ?: false) { isChecked ->
                        taskStatusMap = taskStatusMap.toMutableMap().apply {
                            this[mitglied.id] = isChecked
                        }
                        hasChanges = true
                    }
                }
            }
            if (hasChanges) {
                Button(
                    onClick = {
                        taskStatusMap.forEach { (mitgliedId, isChecked) ->
                            mitgliedRepository.updateMitgliedAufgabeErreicht(mitgliedId, taskId, isChecked)
                        }
                        hasChanges = false
                        onBackToTasks()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Änderungen speichern")
                }
            } else {
                Button(onClick = onBackToTasks) {
                    Text("Zurück zu Aufgaben")
                }
            }
        }
    }
}

@Composable
fun MitgliedRow(mitglied: Mitglied, isTaskCompleted: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "${mitglied.vorname} ${mitglied.nachname}",
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = isTaskCompleted,
            onCheckedChange = onCheckedChange
        )
    }
}
