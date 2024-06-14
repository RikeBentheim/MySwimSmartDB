package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.AufgabeRepository
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
                contentDescription = stringResource(id = R.string.show_details),
                modifier = Modifier.clickable { onTaskSelected(task) }
            )
        }
        if (isDescriptionVisible) {
            Text(text = task.beschreibung, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TasksTab(levelId: Int, kursId: Int, onTaskSelected: (Aufgabe) -> Unit, navController: NavHostController) {
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
            TaskItem(task = task, onTaskSelected = {
                onTaskSelected(it)
                navController.navigate("mitgliedAufgabeTab/${task.id}")
            })
        }
    }
}

