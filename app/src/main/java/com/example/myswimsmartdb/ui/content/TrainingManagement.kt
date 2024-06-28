package com.example.myswimsmartdb.ui.content

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.Reposetory.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level // Importiere Level
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.ui.Composable.AddMemberScreen
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.Platinum

import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun TrainingManagement(
    course: Kurs,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository,
    onEndEditing: () -> Unit
) {
    // Kontext für den Zugriff auf Ressourcen
    val context = LocalContext.current

    // Coroutine Scope
    val coroutineScope = rememberCoroutineScope()

    // Abrufen der Trainings und Mitglieder für den Kurs
    var trainings by remember { mutableStateOf(emptyList<Training>()) }
    var mitglieder by remember { mutableStateOf(emptyList<Mitglied>()) }

    // Initiales Laden der Daten
    LaunchedEffect(course.id) {
        coroutineScope.launch {
            trainings = trainingRepository.getTrainingsByKursId(course.id)
            mitglieder = mitgliedRepository.getMitgliederByKursId(course.id)
        }
    }

    // Zustandsvariablen zur Verwaltung des ausgewählten Trainings und Mitglieds, Nachrichten und des ausgewählten Datums
    var selectedTraining by remember { mutableStateOf<Training?>(null) }
    var selectedMember by remember { mutableStateOf<Mitglied?>(null) }
    var message by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf("") }
    var showAddMemberScreen by remember { mutableStateOf(false) } // Zustandsvariable für das Hinzufügen eines neuen Mitglieds

    if (showAddMemberScreen) {
        AddMemberScreen(
            kursId = course.id,
            selectedLevel = Level(course.levelId, course.levelName, listOf()),
            mitgliedRepository = mitgliedRepository,
            onFinish = {
                showAddMemberScreen = false
                coroutineScope.launch {
                    mitglieder = mitgliedRepository.getMitgliederByKursId(course.id) // Mitglieder aktualisieren
                }
            },
            existingMitglied = selectedMember // Wenn ein Mitglied bearbeitet wird, wird es an das AddMemberScreen übergeben
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Dropdown zur Auswahl eines Trainingstermins
                StringSelectionDropdown(
                    label = "Bitte einen Termin auswählen:",
                    options = trainings.map { it.datumString },
                    selectedOption = selectedTraining?.datumString ?: "",
                    onOptionSelected = { trainingDatum ->
                        selectedTraining = trainings.find { it.datumString == trainingDatum }
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Wenn ein Training ausgewählt ist, wird eine Schaltfläche zum Löschen angezeigt
                selectedTraining?.let {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                selectedTraining?.let { training ->
                                    trainingRepository.deleteTraining(training.id)
                                    trainingRepository.deleteAnwesenheitByTrainingId(training.id)
                                    selectedTraining = null
                                    message = "Der Termin wurde gelöscht."
                                    trainings = trainingRepository.getTrainingsByKursId(course.id) // Trainings aktualisieren
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Termin Löschen", color = Platinum)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Nachricht anzeigen, falls vorhanden
                if (message.isNotEmpty()) {
                    Text(text = message, style = MaterialTheme.typography.bodyLarge, color = IndigoDye)
                }

                // Schaltfläche zum Hinzufügen eines neuen Trainingstermins
                Button(
                    onClick = {
                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _, year: Int, month: Int, dayOfMonth: Int ->
                                calendar.set(year, month, dayOfMonth)
                                selectedDate = dateFormat.format(calendar.time)
                                coroutineScope.launch {
                                    val newTraining = Training(0, selectedDate, "")
                                    val newTrainingId = trainingRepository.insertTraining(newTraining, course.id)
                                    mitglieder.forEach { mitglied ->
                                        trainingRepository.insertAnwesenheit(mitglied.id, newTrainingId)
                                    }
                                    message = "Neuer Termin wurde hinzugefügt."
                                    trainings = trainingRepository.getTrainingsByKursId(course.id) // Trainings aktualisieren
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePickerDialog.show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Neuen Termin hinzufügen", color = Platinum)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dropdown zur Auswahl eines Mitglieds
                StringSelectionDropdown(
                    label = "Bitte ein Mitglied auswählen:",
                    options = mitglieder.map { "${it.vorname} ${it.nachname}" },
                    selectedOption = selectedMember?.let { "${it.vorname} ${it.nachname}" } ?: "",
                    onOptionSelected = { memberName ->
                        selectedMember = mitglieder.find { "${it.vorname} ${it.nachname}" == memberName }
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Wenn ein Mitglied ausgewählt ist, werden Schaltflächen zum Löschen oder Bearbeiten angezeigt
                selectedMember?.let {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                selectedMember?.let { member ->
                                    mitgliedRepository.deleteMitglied(member.id)
                                    selectedMember = null
                                    message = "Das Mitglied wurde gelöscht."
                                    mitglieder = mitgliedRepository.getMitgliederByKursId(course.id) // Mitglieder aktualisieren
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Mitglied Löschen", color = Platinum)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // Bearbeitungslogik für Mitglieder
                            showAddMemberScreen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Mitglied Bearbeiten", color = Platinum)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Nachricht anzeigen, falls vorhanden
                if (message.isNotEmpty()) {
                    Text(text = message, style = MaterialTheme.typography.bodyLarge, color = IndigoDye)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Schaltfläche zum Hinzufügen eines neuen Mitglieds
                Button(
                    onClick = {
                        showAddMemberScreen = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Neues Mitglied hinzufügen", color = Platinum)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Schaltfläche zum Beenden der Kursbearbeitung
                Button(
                    onClick = {
                        onEndEditing()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Kurs Bearbeiten Beenden", color = Platinum)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TrainingManagementPreview() {
    val context = LocalContext.current
    val dummyLevel = Level(id = 1, name = "Bronze", aufgaben = listOf())
    val dummyCourse = Kurs(id = 1, name = "Dummy Kurs", levelId = dummyLevel.id, levelName = dummyLevel.name)
    val dummyTrainingRepository = TrainingRepository(context)
    val dummyMitgliedRepository = MitgliedRepository(context)

    TrainingManagement(
        course = dummyCourse,
        trainingRepository = dummyTrainingRepository,
        mitgliedRepository = dummyMitgliedRepository,
        onEndEditing = {}
    )
}
