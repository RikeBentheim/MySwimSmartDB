package com.example.myswimsmartdb.ui.Composable.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.db.entities.Stoppuhr
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.StoppuhrRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.content.MitgliederVerwaltung
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoppuhrContent(mitglieder: List<Mitglied>, navController: NavHostController, sharedViewModel: SharedViewModel) {
    var canNavigate by remember { mutableStateOf(true) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Handle back press
    BackHandler {
        if (!canNavigate) {
            showConfirmationDialog = true
        } else {
            navController.popBackStack()
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = "Bestätigung erforderlich") },
            text = { Text(text = "Sie haben noch nicht gespeicherte Zeiten. Möchten Sie die Seite wirklich verlassen?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmationDialog = false
                    canNavigate = true
                    navController.popBackStack()
                }) {
                    Text("Verlassen")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        if (mitglieder.isEmpty()) {
            MitgliederVerwaltung(sharedViewModel)
        } else {
            MitgliederStoppuhrVerwaltung(mitglieder, navController, sharedViewModel) { running ->
                canNavigate = !running
            }
        }
    }
}

@Composable
fun MitgliederVerwaltung(sharedViewModel: SharedViewModel, innerPadding: PaddingValues = PaddingValues()) {
    val stoppuhren = remember { mutableStateListOf<Stoppuhr>() }
    var vorname by remember { mutableStateOf("") }
    var nachname by remember { mutableStateOf("") }
    var idCounter by remember { mutableStateOf(1) }

    Column(modifier = Modifier.padding(innerPadding)) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = vorname,
                onValueChange = { vorname = it },
                label = { Text(stringResource(id = R.string.vorname)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
                value = nachname,
                onValueChange = { nachname = it },
                label = { Text(stringResource(id = R.string.nachname)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = {
                if (vorname.isNotBlank() && nachname.isNotBlank()) {
                    stoppuhren.add(Stoppuhr(idCounter++, idCounter, vorname, nachname))
                    vorname = ""
                    nachname = ""
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.mitglied_hinzufuegen))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(stoppuhren) { index, stoppuhr ->
                StoppuhrMitTimer(stoppuhr, onDelete = { stoppuhren.removeAt(index) }, sharedViewModel, onRunningStateChange = {})
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun MitgliederStoppuhrVerwaltung(
    mitglieder: List<Mitglied>,
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onRunningStateChange: (Boolean) -> Unit
) {
    val stoppuhren = remember { mutableStateListOf<Stoppuhr>() }

    LaunchedEffect(mitglieder) {
        stoppuhren.clear()
        mitglieder.forEach { mitglied ->
            stoppuhren.add(Stoppuhr(
                id = mitglied.id,
                mitgliedId = mitglied.id,
                vorname = mitglied.vorname,
                nachname = mitglied.nachname,
                datum = Date()
            ))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(stoppuhren) { index, stoppuhr ->
                StoppuhrMitTimer(stoppuhr, onDelete = { stoppuhren.removeAt(index) }, sharedViewModel, onRunningStateChange)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(
            onClick = {
                val selectedCourse = sharedViewModel.selectedCourse
                val selectedDate = sharedViewModel.selectedTraining?.datum ?: ""
                navController.navigate("kursVerwaltungBack/${selectedCourse?.id ?: 0}/$selectedDate") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text("Zurück zur Kursverwaltung")
        }
    }
}

@Composable
fun StoppuhrMitTimer(
    stoppuhr: Stoppuhr,
    onDelete: () -> Unit,
    sharedViewModel: SharedViewModel,
    onRunningStateChange: (Boolean) -> Unit
) {
    var isRunning by remember { mutableStateOf(stoppuhr.running) }
    var time by remember { mutableStateOf(stoppuhr.zeit.toDuration(DurationUnit.MILLISECONDS)) }
    var showDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(isRunning) {
        onRunningStateChange(isRunning)
        if (isRunning) {
            coroutineScope.launch {
                while (isRunning) {
                    delay(1000L)
                    time += 1.toDuration(DurationUnit.SECONDS)
                    stoppuhr.addTime(1000L)
                }
            }
        }
    }

    // Rest of the function remains the same
}
