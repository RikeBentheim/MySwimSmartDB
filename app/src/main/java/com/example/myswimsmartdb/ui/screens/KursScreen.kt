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
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.content.CourseDetails
import com.example.myswimsmartdb.ui.content.TrainingManagement

@Composable
fun KursScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)

    val courses = kursRepository.getAllKurseWithDetails()
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var editMode by remember { mutableStateOf(false) }

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
                TrainingManagement(
                    course = course,
                    trainingRepository = trainingRepository,
                    mitgliedRepository = mitgliedRepository
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
