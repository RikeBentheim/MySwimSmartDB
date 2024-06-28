package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.Reposetory.TrainingRepository
import com.example.layout.ui.layout.components.DatePickerButton
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.R
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKurstermineScreen(
    kursId: Int,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository,
    onFinish: () -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showInputFields by remember { mutableStateOf(true) }
    var generatedDates by remember { mutableStateOf(listOf<String>()) }
    val changesSaved = stringResource(id = R.string.save_changes)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showInputFields) {
            DatePickerButton(
                selectedDate = startDate,
                onDateSelected = { date ->
                    startDate = date
                }
            )

            var datesText by remember { mutableStateOf("") }

            Button(
                onClick = {
                    val dates = generateDates(startDate)
                    generatedDates = dates
                    datesText = dates.joinToString("\n")
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.generate_dates))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                itemsIndexed(generatedDates) { index, date ->
                    DatePickerButton(
                        selectedDate = date,
                        onDateSelected = { newDate ->
                            generatedDates = generatedDates.toMutableList().apply {
                                this[index] = newDate
                            }
                            datesText = generatedDates.joinToString("\n")
                        }
                    )
                }
            }

            OutlinedTextField(
                value = datesText,
                onValueChange = { datesText = it },
                label = { Text(stringResource(id = R.string.dates)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                maxLines = 10,
                readOnly = true
            )

            var message by remember { mutableStateOf("") }

            Button(
                onClick = {
                    coroutineScope.launch {
                        val dates = datesText.split("\n")
                        saveDates(dates, kursId, trainingRepository, mitgliedRepository)
                        message = changesSaved
                        showInputFields = false
                        onFinish()
                    }
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = R.string.save_changes))
            }

            if (message.isNotEmpty()) {
                Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                generatedDates.forEach { date ->
                    Text(text = date, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

fun generateDates(startDate: String): List<String> {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val dates = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    calendar.time = sdf.parse(startDate) ?: return emptyList()
    for (i in 0 until 10) {
        dates.add(sdf.format(calendar.time))
        calendar.add(Calendar.DATE, 7)
    }
    return dates
}

suspend fun saveDates(dates: List<String>, kursId: Int, trainingRepository: TrainingRepository, mitgliedRepository: MitgliedRepository) {
    val trainings = dates.map { date ->
        Training(
            id = 0,
            datumString = date,
            bemerkung = ""
        )
    }
    trainings.forEach { training ->
        val trainingId = trainingRepository.insertTraining(training, kursId)
        val mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
        mitglieder.forEach { mitglied ->
            trainingRepository.insertAnwesenheit(mitglied.id, trainingId)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddKurstermineScreenPreview() {
    val context = LocalContext.current
    val dummyTrainingRepository = TrainingRepository(context)
    val dummyMitgliedRepository = MitgliedRepository(context)

    AddKurstermineScreen(
        kursId = 1,
        trainingRepository = dummyTrainingRepository,
        mitgliedRepository = dummyMitgliedRepository,
        onFinish = {}
    )
}
