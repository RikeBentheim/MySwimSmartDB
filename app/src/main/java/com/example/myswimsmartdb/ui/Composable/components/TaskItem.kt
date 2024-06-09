package com.example.myswimsmartdb.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.entities.Aufgabe

@Composable
fun TaskItem(task: Aufgabe, onTaskSelected: (Aufgabe) -> Unit, modifier: Modifier = Modifier) {
    var isDescriptionVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isDescriptionVisible = !isDescriptionVisible }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = task.aufgabe, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Show Details",
                modifier = Modifier.clickable { onTaskSelected(task) }
            )
        }
        if (isDescriptionVisible) {
            Text(text = task.beschreibung, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
