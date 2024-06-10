package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitgliedAufgabeTabAlt(
    taskId: Int,
    kursId: Int,
    mitgliedRepository: MitgliedRepository,
    onBackToTasks: () -> Unit
) {
    var mitglieder by remember { mutableStateOf<List<Mitglied>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasChanges by remember { mutableStateOf(false) }
    val changes = remember { mutableStateMapOf<Int, Boolean>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(kursId) {
        try {
            val mitgliederResult = mitgliedRepository.getFullMitgliederDetailsByKursId(kursId)
            mitglieder = mitgliederResult

            // Initialisieren Sie die Änderungsmap mit den aktuellen 'erreicht'-Werten
            mitgliederResult.forEach { mitglied ->
                mitglied.aufgaben.forEach { aufgabe ->
                    if (aufgabe.id == taskId) {
                        changes[mitglied.id] = aufgabe.erledigt
                    }
                }
            }
        } catch (e: Exception) {
            // Log the error if needed
            println("Error loading data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mitglieder) { mitglied ->
                    val isChecked = changes[mitglied.id] ?: false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${mitglied.vorname} ${mitglied.nachname}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                changes[mitglied.id] = checked
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
                        coroutineScope.launch {
                            try {
                                changes.forEach { (mitgliedId, erreicht) ->
                                    mitgliedRepository.updateMitgliedAufgabeErreicht(mitgliedId, taskId, erreicht)
                                }
                                hasChanges = false
                                onBackToTasks()
                            } catch (e: Exception) {
                                // Log the error if needed
                                println("Error updating data: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.save_changes))
                }
            } else {
                Button(onClick = onBackToTasks) {
                    Text(stringResource(id = R.string.back_to_tasks))
                }
            }
        }
    }
}
