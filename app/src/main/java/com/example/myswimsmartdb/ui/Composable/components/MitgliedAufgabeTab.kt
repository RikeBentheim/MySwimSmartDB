package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitgliedAufgabeTab(
    taskId: Int,
    kursId: Int,
    mitgliedRepository: MitgliedRepository,
    onBackToTasks: () -> Unit,
    navController: NavHostController // Add this parameter
) {
    var mitglieder by remember { mutableStateOf<List<Mitglied>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasChanges by remember { mutableStateOf(false) }
    val changes = remember { mutableStateMapOf<Int, Boolean>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loadMitglieder(kursId, mitgliedRepository) { geladeneMitglieder ->
            mitglieder = geladeneMitglieder
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mitglieder) { mitglied ->
                    val aufgabe = mitglied.aufgaben.find { it.id == taskId }
                    if (aufgabe != null) {
                        var erledigt by remember { mutableStateOf(aufgabe.erledigt) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${mitglied.vorname} ${mitglied.nachname}")
                            Checkbox(
                                checked = erledigt,
                                onCheckedChange = { checked ->
                                    erledigt = checked
                                    changes[mitglied.id] = checked
                                    hasChanges = true
                                    Log.d("MitgliedAufgabeTab", "Änderung für Mitglied ${mitglied.id} auf $checked gesetzt")
                                }
                            )
                        }
                    }
                }
            }
            if (hasChanges) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            changes.forEach { (mitgliedId, erledigt) ->
                                Log.d("MitgliedAufgabeTab", "Speichere Änderung für Mitglied $mitgliedId auf $erledigt")
                                mitgliedRepository.updateMitgliedAufgabeErreicht(mitgliedId, taskId, erledigt)
                            }
                            loadMitglieder(kursId, mitgliedRepository) { geladeneMitglieder ->
                                mitglieder = geladeneMitglieder
                                hasChanges = false
                            }
                            onBackToTasks()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.save_changes))
                }
            } else {
                Button(
                    onClick = onBackToTasks,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.back_to_tasks))
                }
            }
        }
    }
}

fun loadMitglieder(
    kursId: Int,
    mitgliedRepository: MitgliedRepository,
    onMitgliederLoaded: (List<Mitglied>) -> Unit
) {
    val coroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
    coroutineScope.launch {
        val mitglieder = mitgliedRepository.getFullMitgliederDetailsByKursId(kursId)
        onMitgliederLoaded(mitglieder)
        Log.d("MitgliedAufgabeTab", "Mitglieder geladen: $mitglieder")
    }
}
