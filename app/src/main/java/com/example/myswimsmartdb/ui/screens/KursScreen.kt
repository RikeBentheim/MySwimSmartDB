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

@Composable
fun KursScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)

    val courses = kursRepository.getAllKurseWithDetails()
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
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
            CourseDetails(
                course = course,
                trainingRepository = trainingRepository,
                mitgliedRepository = mitgliedRepository
            )
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

        Text(text = "Trainings:", style = MaterialTheme.typography.bodyLarge)
        trainings.forEach { training ->
            Text(text = training.datumString, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Members:", style = MaterialTheme.typography.bodyLarge)
        mitglieder.forEach { member ->
            Text(text = "${member.vorname} ${member.nachname}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun KursScreenPreview() {
    KursScreen(navController = rememberNavController())
}
