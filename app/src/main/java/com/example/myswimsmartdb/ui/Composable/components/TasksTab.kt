package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
            Text(text = task.aufgabe, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = stringResource(id = R.string.show_details),
                modifier = Modifier.clickable { onTaskSelected(task) }
            )
        }
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

    LaunchedEffect(levelId) {
        tasks = aufgabeRepository.getAufgabenByLevelId(levelId)
    }

    LaunchedEffect(kursId) {
        mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskSelected = onTaskSelected,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Stoppuhr starten")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Mitglieder auswählen") },
                text = {
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
                                Text("${mitglied.vorname} ${mitglied.nachname}")
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

