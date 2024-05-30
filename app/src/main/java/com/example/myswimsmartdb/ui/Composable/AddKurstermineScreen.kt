package com.example.myswimsmartdb.ui.Composable

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Training
import java.text.SimpleDateFormat
import java.util.*

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DatePickerButton(
            labelText = "Startdatum",
            initialDate = startDate,
            onDateSelected = { date ->
                startDate = date
            }
        )

        var generatedDates by remember { mutableStateOf(listOf<String>()) }
        var datesText by remember { mutableStateOf("") }

        Button(
            onClick = {
                val dates = generateDates(startDate)
                generatedDates = dates
                datesText = dates.joinToString("\n")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Termine generieren")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            itemsIndexed(generatedDates) { index, date ->
                DatePickerButton(
                    labelText = "Termin ${index + 1}",
                    initialDate = date,
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
            label = { Text("Termine") },
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
                val dates = datesText.split("\n")
                saveDates(dates, kursId, trainingRepository, mitgliedRepository)
                message = "Termine erfolgreich gespeichert"
                onFinish()
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Termine speichern")
        }

        if (message.isNotEmpty()) {
            Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun DatePickerButton(
    labelText: String,
    initialDate: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(initialDate) }
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = labelText,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
        )

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text(labelText) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            val date = sdf.format(calendar.time)
                            selectedDate = date
                            onDateSelected(date)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
            trailingIcon = {
                IconButton(onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            val date = sdf.format(calendar.time)
                            selectedDate = date
                            onDateSelected(date)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            }
        )
    }
}

fun generateDates(startDate: String): List<String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dates = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    calendar.time = sdf.parse(startDate) ?: return emptyList()
    for (i in 0 until 10) {
        dates.add(sdf.format(calendar.time))
        calendar.add(Calendar.DATE, 7)
    }
    return dates
}

fun saveDates(dates: List<String>, kursId: Int, trainingRepository: TrainingRepository, mitgliedRepository: MitgliedRepository) {
    val trainings = dates.map { date ->
        Training(
            id = 0,
            datumString = date,
            bemerkung = ""
        )
    }
    trainings.forEach { training ->
        val trainingId = trainingRepository.insertTraining(training, kursId)
        mitgliedRepository.getMitgliederByKursId(kursId).forEach { mitglied ->
            trainingRepository.insertAnwesenheit(mitglied.id, trainingId)
        }
    }
}
