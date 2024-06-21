package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.AufgabeRepository
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Mitglied

@Composable
fun TaskItem(
    task: Aufgabe,
    onTaskSelected: (Aufgabe) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDescriptionVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isDescriptionVisible = !isDescriptionVisible }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Zeigt den Namen der Aufgabe an
            Text(text = task.aufgabe, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            // Icon zum Auswählen der Aufgabe
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(id = R.string.show_details),
                modifier = Modifier.clickable { onTaskSelected(task) }
            )
        }
        // Sichtbarkeit der Aufgabenbeschreibung umschalten
        if (isDescriptionVisible) {
            Text(text = task.beschreibung, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TasksTab(levelId: Int, kursId: Int, onTaskSelected: (Aufgabe) -> Unit, navController: NavHostController) {
    val context = LocalContext.current
    val aufgabeRepository = AufgabeRepository(context)
    val mitgliedRepository = MitgliedRepository(context)
    var tasks by remember { mutableStateOf(listOf<Aufgabe>()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMitglieder by remember { mutableStateOf<List<Mitglied>>(emptyList()) }
    var mitglieder by remember { mutableStateOf(emptyList<Mitglied>()) }
    var selectedTask by remember { mutableStateOf<Aufgabe?>(null) } // Zustand, um die ausgewählte Aufgabe zu speichern

    // Aufgaben laden, wenn sich die levelId ändert
    LaunchedEffect(levelId) {
        tasks = aufgabeRepository.getAufgabenByLevelId(levelId)
    }

    // Mitglieder laden, wenn sich die kursId ändert
    LaunchedEffect(kursId) {
        mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // LazyColumn zum Anzeigen der Aufgabenliste
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskSelected = {
                        selectedTask = it // Setzt die ausgewählte Aufgabe, wenn das Icon angeklickt wird
                        showDialog = true // Zeigt den Dialog zum Auswählen der Mitglieder an
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button zum Starten der Stoppuhr
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Stoppuhr starten")
        }

        // Dialog zum Auswählen der Mitglieder
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Mitglieder auswählen") },
                text = {
                    Column {
                        // Zeigt den Namen der ausgewählten Aufgabe über der Liste an
                        selectedTask?.let {
                            Text("Ausgewählte Tätigkeit: ${it.aufgabe}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // LazyColumn zum Anzeigen der Mitgliederliste
                        LazyColumn {
                            items(mitglieder) { mitglied ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedMitglieder = if (selectedMitglieder.contains(mitglied)) {
                                                selectedMitglieder - mitglied
                                            } else {
                                                selectedMitglieder + mitglied
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Checkbox für jedes Mitglied
                                    Checkbox(
                                        checked = selectedMitglieder.contains(mitglied),
                                        onCheckedChange = { checked ->
                                            selectedMitglieder = if (checked) {
                                                selectedMitglieder + mitglied
                                            } else {
                                                selectedMitglieder - mitglied
                                            }
                                        }
                                    )
                                    // Zeigt den Namen des Mitglieds an
                                    Text("${mitglied.vorname} ${mitglied.nachname}")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        navController.navigate("stoppuhr/${selectedMitglieder.joinToString(",") { it.id.toString() }}")
                        showDialog = false
                    }) {
                        Text("Starten")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }
}
