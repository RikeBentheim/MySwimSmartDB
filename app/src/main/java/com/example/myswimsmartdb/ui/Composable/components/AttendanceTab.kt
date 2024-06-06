package com.example.myswimsmartdb.ui.Composable.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AttendanceTab() {
    val attendees = listOf("Teilnehmer 1", "Teilnehmer 2", "Teilnehmer 3")
    Column {
        attendees.forEach { attendee ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Text(text = attendee, modifier = Modifier.weight(1f))
                Checkbox(checked = false, onCheckedChange = { /* Anwesenheit ändern Logik hier */ })
            }
        }
    }
}
