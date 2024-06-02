package com.example.layout.ui.layout.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerButton(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf(selectedDate) }
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    Column(modifier = modifier.padding(8.dp)) {
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Geburtsdatum") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            val newDate = sdf.format(calendar.time)
                            date = newDate
                            onDateSelected(newDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}
