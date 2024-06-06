package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.AufgabeRepository
import com.example.myswimsmartdb.db.entities.Aufgabe

@Composable
fun TaskItem(task: Aufgabe, modifier: Modifier = Modifier) {
    var isDescriptionVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { isDescriptionVisible = !isDescriptionVisible }
    ) {
        Text(text = task.aufgabe, style = MaterialTheme.typography.bodyLarge)
        if (isDescriptionVisible) {
            Text(text = task.beschreibung, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TasksTab(levelId: Int) {
    val context = LocalContext.current
    val aufgabeRepository = AufgabeRepository(context)
    var tasks by remember { mutableStateOf(listOf<Aufgabe>()) }

    LaunchedEffect(levelId) {
        tasks = aufgabeRepository.getAufgabenByLevelId(levelId)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(tasks) { task ->
            TaskItem(task = task)
        }
    }
}
