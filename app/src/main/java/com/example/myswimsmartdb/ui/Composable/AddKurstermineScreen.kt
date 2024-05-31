
// AddKurstermineScreen.kt
package com.example.myswimsmartdb.ui.Composable

import android.app.DatePickerDialog
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
