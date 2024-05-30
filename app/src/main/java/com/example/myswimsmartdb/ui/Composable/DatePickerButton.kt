package com.example.layout.ui.layout.components

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import com.example.myswimsmartdb.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerButton(
    labelText: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    Column(modifier = modifier.padding(AppTheme.padding)) {
        Text(
            text = labelText,
            modifier = Modifier.padding(horizontal = AppTheme.padding),
            style = MaterialTheme.typography.bodySmall.copy(color = AppTheme.textColorDunkel)
        )

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text(labelText) },
            readOnly = true,
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.padding)
        )
    }
}
