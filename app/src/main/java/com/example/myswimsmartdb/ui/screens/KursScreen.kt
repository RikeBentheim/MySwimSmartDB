package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.content.CourseDetails
import com.example.myswimsmartdb.ui.content.MitgliederManagement
import com.example.myswimsmartdb.ui.content.TrainingManagement
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.Platinum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KursScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)

    // Liste der verfügbaren Kurse laden
    var courses by remember { mutableStateOf(kursRepository.getAllKurseWithDetails()) }
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var editMode by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") } // Zustandsvariable für Nachrichten

    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bild
            Image(
                painter = painterResource(id = R.drawable.adobestock_288862937),
                contentDescription = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Titeltext
            Text(
                text = stringResource(id = R.string.schwimmverein_haltern),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Kurs Bearbeiten", color = Platinum)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button zum Löschen des ausgewählten Kurses
                    Button(
                        onClick = {
                            kursRepository.deleteKursWithDetails(course.id)
                            selectedCourse = null
                            editMode = false
                            message = "Kurs wurde gelöscht." // Nachricht setzen
                            courses = kursRepository.getAllKurseWithDetails() // Liste der Kurse aktualisieren
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Kurs Löschen", color = Platinum)
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
                        Spacer(modifier = Modifier.height(20.dp))
                        MitgliederManagement(
                            kursId = course.id,
                            mitgliedRepository = mitgliedRepository,
                            selectedLevel = Level(course.levelId, course.levelName, listOf())
                        )
                    }
                }
            }

            // Nachricht anzeigen, falls vorhanden
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = IndigoDye,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun KursScreenPreview() {
    KursScreen(navController = rememberNavController())
}
