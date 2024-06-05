package com.example.myswimsmartdb.ui.screens

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
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.content.CourseDetails
import com.example.myswimsmartdb.ui.content.MitgliederManagement
import com.example.myswimsmartdb.ui.content.TrainingManagement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KursScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)

    // Liste der verfügbaren Kurse laden
    val courses = kursRepository.getAllKurseWithDetails()
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var editMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!editMode) {
            // Dropdown-Menü zur Auswahl eines Kurses
            StringSelectionDropdown(
                label = "Bitte einen Kurs auswählen:",
                options = courses.map { it.name },
                selectedOption = selectedCourse?.name ?: "",
                onOptionSelected = { courseName ->
                    selectedCourse = courses.find { it.name == courseName }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Kursdetails und Bearbeiten-Button anzeigen, wenn ein Kurs ausgewählt wurde
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

                // Details des ausgewählten Kurses anzeigen
                CourseDetails(
                    course = course,
                    trainingRepository = trainingRepository,
                    mitgliedRepository = mitgliedRepository
                )
            }
        } else {
            // Trainings- und Mitgliedermanagement anzeigen, wenn im Bearbeiten-Modus
            selectedCourse?.let { course ->
                Column {
                    TrainingManagement(
                        course = course,
                        trainingRepository = trainingRepository,
                        mitgliedRepository = mitgliedRepository
                    )

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
