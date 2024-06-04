package com.example.myswimsmartdb.ui.content

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrainingManagement(
    course: Kurs,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository
) {
    val context = LocalContext.current
    val trainings = trainingRepository.getTrainingsByKursId(course.id)
    val mitglieder = mitgliedRepository.getMitgliederByKursId(course.id)

    var selectedTraining by remember { mutableStateOf<Training?>(null) }
    var selectedMember by remember { mutableStateOf<Mitglied?>(null) }
    var message by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf("") }

    Column {
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

        selectedTraining?.let {
            Button(
                onClick = {
                    selectedTraining?.let { training ->
                        trainingRepository.deleteTraining(training.id)
                        trainingRepository.deleteAnwesenheitByTrainingId(training.id)
                        selectedTraining = null
                        message = "Der Termin wurde gelöscht."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Termin Löschen")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (message.isNotEmpty()) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }

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

        selectedMember?.let {
            Button(
                onClick = {
                    selectedMember?.let { member ->
                        mitgliedRepository.deleteMitglied(member.id)
                        selectedMember = null
                        message = "Das Mitglied wurde gelöscht."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mitglied Löschen")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // Handle edit member logic
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mitglied Bearbeiten")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (message.isNotEmpty()) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
