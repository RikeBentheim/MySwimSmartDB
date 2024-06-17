package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myswimsmartdb.db.entities.Stoppuhr
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoppuhrContent(mitglieder: List<Mitglied>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (mitglieder.isEmpty()) {
            MitgliederVerwaltung()
        } else {
            MitgliederStoppuhrVerwaltung(mitglieder)
        }
    }
}

@Composable
fun MitgliederVerwaltung(innerPadding: PaddingValues = PaddingValues()) {
    // Liste für Stoppuhren
    val stoppuhren = remember { mutableStateListOf<Stoppuhr>() }
    // Zustände für Vorname und Nachname
    var vorname by remember { mutableStateOf("") }
    var nachname by remember { mutableStateOf("") }
    // ID-Zähler für die Stoppuhren
    var idCounter by remember { mutableStateOf(1) }

    Column(modifier = Modifier.padding(innerPadding)) {
        Spacer(modifier = Modifier.height(30.dp))

        // Zeile für Eingabefelder und Hinzufügen-Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Eingabefeld für den Vornamen
            OutlinedTextField(
                value = vorname,
                onValueChange = { vorname = it },
                label = { Text(stringResource(id = R.string.vorname)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Eingabefeld für den Nachnamen
            OutlinedTextField(
                value = nachname,
                onValueChange = { nachname = it },
                label = { Text(stringResource(id = R.string.nachname)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Button zum Hinzufügen einer neuen Stoppuhr
            IconButton(onClick = {
                if (vorname.isNotBlank() && nachname.isNotBlank()) {
                    stoppuhren.add(Stoppuhr(idCounter++, idCounter, vorname, " ", nachname))
                    vorname = ""
                    nachname = ""
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.mitglied_hinzufuegen))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste der Stoppuhren
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(stoppuhren) { index, stoppuhr ->
                // Einzelne Zeile für jede Stoppuhr
                StoppuhrMitTimer(stoppuhr, onDelete = { stoppuhren.removeAt(index) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MitgliederStoppuhrVerwaltung(mitglieder: List<Mitglied>) {
    // Liste für Stoppuhren initialisieren
    val stoppuhren = remember { mutableStateListOf<Stoppuhr>() }

    // Effekt, um die Liste der Stoppuhren mit den übergebenen Mitgliedern zu füllen
    LaunchedEffect(mitglieder) {
        stoppuhren.clear()
        mitglieder.forEach { mitglied ->
            stoppuhren.add(Stoppuhr(mitglied.id, mitglied.id, mitglied.vorname, "", mitglied.nachname))
        }
    }

    // Anzeige der Liste der Stoppuhren
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(stoppuhren) { index, stoppuhr ->
            StoppuhrMitTimer(stoppuhr, onDelete = { stoppuhren.removeAt(index) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StoppuhrMitTimer(stoppuhr: Stoppuhr, onDelete: () -> Unit) {
    // Zustände für den Timer und den Dialog
    var isRunning by remember { mutableStateOf(stoppuhr.running) }
    var time by remember { mutableStateOf(stoppuhr.zeit.toDuration(DurationUnit.MILLISECONDS)) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Timer-Logik
    LaunchedEffect(isRunning) {
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

    // Dialog zum Zurücksetzen des Timers oder Löschen des Mitglieds
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.optionen)) },
            text = {
                Column {
                    Button(
                        onClick = {
                            stoppuhr.reset()
                            time = stoppuhr.zeit.toDuration(DurationUnit.MILLISECONDS)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(stringResource(id = R.string.timer_neu_starten))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onDelete()
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(stringResource(id = R.string.mitglied_loeschen))
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Layout der Stoppuhrzeile
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.wasser),
            contentDescription = null,
            modifier = Modifier.matchParentSize().alpha(0.2f),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, IndigoDye),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Box für das Zurücksetzen/Löschen der Stoppuhr
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(SkyBlue)
                    .clickable { showDialog = true }
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "⟳",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Anzeige des Namens des Mitglieds
            Text("${stoppuhr.vorname} ${stoppuhr.nachname}", modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(16.dp))

            // Button zum Starten/Stoppen des Timers
            Button(
                onClick = {
                    if (isRunning) {
                        stoppuhr.stop()
                    } else {
                        stoppuhr.start()
                    }
                    isRunning = !isRunning
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Cerulean else SkyBlue
                ),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.height(50.dp)
            ) {
                Text(if (isRunning) stringResource(id = R.string.stop) else stringResource(id = R.string.start))
            }

            // Anzeige der gestoppten Zeit
            val hours = (time.inWholeSeconds / 3600).toString().padStart(2, '0')
            val minutes = ((time.inWholeSeconds % 3600) / 60).toString().padStart(2, '0')
            val seconds = (time.inWholeSeconds % 60).toString().padStart(2, '0')

            Text(
                text = "$hours:$minutes:$seconds",
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .requiredWidth(80.dp)
            )
        }
    }
}


