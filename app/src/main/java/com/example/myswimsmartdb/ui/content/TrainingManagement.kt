package com.example.myswimsmartdb.ui.content

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level // Importiere Level
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.ui.Composable.AddMemberScreen
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrainingManagement(
    course: Kurs,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository
) {
    // Kontext für den Zugriff auf Ressourcen
    val context = LocalContext.current

    // Abrufen der Trainings und Mitglieder für den Kurs
    var trainings by remember { mutableStateOf(trainingRepository.getTrainingsByKursId(course.id)) }
    var mitglieder by remember { mutableStateOf(mitgliedRepository.getMitgliederByKursId(course.id)) }

    // Zustandsvariablen zur Verwaltung des ausgewählten Trainings und Mitglieds, Nachrichten und des ausgewählten Datums
    var selectedTraining by remember { mutableStateOf<Training?>(null) }
    var selectedMember by remember { mutableStateOf<Mitglied?>(null) }
    var message by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf("") }
    var showAddMemberScreen by remember { mutableStateOf(false) } // Zustandsvariable für das Hinzufügen eines neuen Mitglieds

    // Hauptspaltenlayout
    Column {
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
                    selectedTraining?.let { training ->
                        trainingRepository.deleteTraining(training.id)
                        trainingRepository.deleteAnwesenheitByTrainingId(training.id)
                        selectedTraining = null
                        message = "Der Termin wurde gelöscht."
                        trainings = trainingRepository.getTrainingsByKursId(course.id) // Trainings aktualisieren
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Termin Löschen")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Nachricht anzeigen, falls vorhanden
        if (message.isNotEmpty()) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }

        // Schaltfläche zum Hinzufügen eines neuen Trainingstermins
        Button(
            onClick = {
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year: Int, month: Int, dayOfMonth: Int ->
                        calendar.set(year, month, dayOfMonth)
                        selectedDate = dateFormat.format(calendar.time)
                        val newTraining = Training(0, selectedDate, "")
                        val newTrainingId = trainingRepository.insertTraining(newTraining, course.id)
                        mitglieder.forEach { mitglied ->
                            trainingRepository.insertAnwesenheit(mitglied.id, newTrainingId)
                        }
                        message = "Neuer Termin wurde hinzugefügt."
                        trainings = trainingRepository.getTrainingsByKursId(course.id) // Trainings aktualisieren
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Neuen Termin hinzufügen")
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
                    selectedMember?.let { member ->
                        mitgliedRepository.deleteMitglied(member.id)
                        selectedMember = null
                        message = "Das Mitglied wurde gelöscht."
                        mitglieder = mitgliedRepository.getMitgliederByKursId(course.id) // Mitglieder aktualisieren
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mitglied Löschen")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // Bearbeitungslogik für Mitglieder
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mitglied Bearbeiten")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Nachricht anzeigen, falls vorhanden
        if (message.isNotEmpty()) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Schaltfläche zum Hinzufügen eines neuen Mitglieds
        Button(
            onClick = {
                showAddMemberScreen = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Neues Mitglied hinzufügen")
        }

        // Wenn die Zustandsvariable true ist, zeige das AddMemberScreen Composable
        if (showAddMemberScreen) {
            AddMemberScreen(
                kursId = course.id,
                selectedLevel = Level(course.levelId, course.levelName, listOf()),
                mitgliedRepository = mitgliedRepository,
                onFinish = {
                    showAddMemberScreen = false
                    mitglieder = mitgliedRepository.getMitgliederByKursId(course.id) // Mitglieder aktualisieren
                }
            )
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
        mitgliedRepository = dummyMitgliedRepository
    )
}
