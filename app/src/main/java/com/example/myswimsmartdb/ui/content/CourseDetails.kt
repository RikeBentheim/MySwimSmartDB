package com.example.myswimsmartdb.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.Reposetory.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.db.entities.Mitglied
import kotlinx.coroutines.launch

@Composable
fun CourseDetails(
    course: Kurs,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository
) {
    var trainings by remember { mutableStateOf(emptyList<Training>()) }
    var mitglieder by remember { mutableStateOf(emptyList<Mitglied>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(course.id) {
        coroutineScope.launch {
            trainings = trainingRepository.getTrainingsByKursId(course.id)
            mitglieder = mitgliedRepository.getMitgliederByKursId(course.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Kursname: ${course.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Level: ${course.levelName}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "Trainings:", style = MaterialTheme.typography.bodyLarge)
                trainings.forEach { training ->
                    Text(text = training.datumString, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = "Mitglieder:", style = MaterialTheme.typography.bodyLarge)
                mitglieder.forEach { member ->
                    Text(text = "${member.vorname} ${member.nachname}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
