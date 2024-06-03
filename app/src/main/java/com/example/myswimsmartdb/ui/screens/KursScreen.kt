package com.example.myswimsmartdb.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun KursScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)

    val courses = kursRepository.getAllKurseWithDetails()
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var selectedTraining by remember { mutableStateOf<Training?>(null) }
    var editMode by remember { mutableStateOf(false) }
    var trainings by remember { mutableStateOf(emptyList<Training>()) }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        if (!editMode) {
            StringSelectionDropdown(
                label = "Bitte einen Kurs auswählen:",
                options = courses.map { it.name },
                selectedOption = selectedCourse?.name ?: "",
                onOptionSelected = { courseName ->
                    selectedCourse = courses.find { it.name == courseName }
                    selectedTraining = null
                    selectedCourse?.let {
                        trainings = trainingRepository.getTrainingsByKursId(it.id)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            selectedCourse?.let { course ->
                Button(
                    onClick = {
                        editMode = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kurs Bearbeiten")
                }

                Spacer(modifier = Modifier.height(20.dp))

                CourseDetails(
                    course = course,
                    trainingRepository = trainingRepository,
                    mitgliedRepository = mitgliedRepository
                )
            }
        } else {
            selectedCourse?.let { course ->
                val mitglieder = mitgliedRepository.getMitgliederByKursId(course.id)

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
                                trainings = trainingRepository.getTrainingsByKursId(course.id)
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

                val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
                val calendar = remember { Calendar.getInstance() }
                var selectedDate by remember { mutableStateOf("") }

                Button(
                    onClick = {
                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                calendar.set(year, month, dayOfMonth)
                                selectedDate = dateFormat.format(calendar.time)
                                val newTraining = Training(0, selectedDate, "")
                                val newTrainingId = trainingRepository.insertTraining(newTraining, course.id)
                                mitglieder.forEach { mitglied ->
                                    trainingRepository.insertAnwesenheit(mitglied.id, newTrainingId)
                                }
                                trainings = trainingRepository.getTrainingsByKursId(course.id)
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

                StringSelectionDropdown(
                    label = "Bitte ein Mitglied auswählen:",
                    options = mitglieder.map { "${it.vorname} ${it.nachname}" },
                    selectedOption = "",
                    onOptionSelected = { /* Handle member selection */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CourseDetails(
    course: Kurs,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository
) {
    val trainings = trainingRepository.getTrainingsByKursId(course.id)
    val mitglieder = mitgliedRepository.getMitgliederByKursId(course.id)

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(text = "Kursname: ${course.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Level: ${course.levelName}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)) {
                Text(text = "Trainings:", style = MaterialTheme.typography.bodyLarge)
                trainings.forEach { training ->
                    Text(text = training.datumString, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)) {
                Text(text = "Mitglieder:", style = MaterialTheme.typography.bodyLarge)
                mitglieder.forEach { member ->
                    Text(text = "${member.vorname} ${member.nachname}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun KursScreenPreview() {
    KursScreen(navController = rememberNavController())
}
