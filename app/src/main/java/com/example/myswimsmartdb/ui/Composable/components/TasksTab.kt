package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myswimsmartdb.db.AufgabeRepository
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.ui.components.TaskItem

@Composable
fun TasksTab(levelId: Int, kursId: Int, navController: NavController, onTaskSelected: (Aufgabe) -> Unit) {
    val context = LocalContext.current
    val aufgabeRepository = AufgabeRepository(context)
    var tasks by remember { mutableStateOf(listOf<Aufgabe>()) }

    LaunchedEffect(levelId) {
        tasks = aufgabeRepository.getAufgabenByLevelId(levelId)
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        items(tasks) { task ->
            TaskItem(task = task, onTaskSelected = onTaskSelected)
        }
    }
}

