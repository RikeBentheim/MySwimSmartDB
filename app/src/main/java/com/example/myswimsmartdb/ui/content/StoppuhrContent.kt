package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.entities.Stoppuhr
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun StoppuhrContent() {
    MitgliederVerwaltung()
}

@Composable
fun MitgliederVerwaltung(innerPadding: PaddingValues = PaddingValues()) {
    val stoppuhren = remember { mutableStateListOf<Stoppuhr>() }
    var vorname by remember { mutableStateOf("") }
    var nachname by remember { mutableStateOf("") }
    var idCounter by remember { mutableStateOf(1) }

    Column(modifier = Modifier.padding(innerPadding)) {
        // Platz für Header-Bild
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Stoppuhr", style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Spacer(modifier = Modifier.height(45.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = vorname,
                onValueChange = { vorname = it },
                label = { Text("Vorname") },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, IndigoDye)
                    .background(Color.Transparent)
                    .padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            TextField(
                value = nachname,
                onValueChange = { nachname = it },
                label = { Text("Nachname") },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, IndigoDye)
                    .background(Color.Transparent)
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(stoppuhren) { _, stoppuhr ->
                StoppuhrMitTimer(stoppuhr)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StoppuhrMitTimer(stoppuhr: Stoppuhr) {
    var isRunning by remember { mutableStateOf(stoppuhr.running) }
    var time by remember { mutableStateOf(stoppuhr.zeit.toDuration(DurationUnit.MILLISECONDS)) }
    val coroutineScope = rememberCoroutineScope()

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, IndigoDye), // Vertikaler Abstand für die Row
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(SkyBlue)
                .padding(start = 8.dp), // Korrigiert von left zu start
            contentAlignment = Alignment.Center
        ) {
            Text("⟳", color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp)) // Abstand zwischen Box und Textfeld

        Text("${stoppuhr.vorname} ${stoppuhr.nachname}", modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(16.dp)) // Abstand zwischen Textfeld und Button

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
            shape = MaterialTheme.shapes.extraSmall, // Eckiger Button
            modifier = Modifier
                .height(50.dp)  // gleiche Höhe wie die Box
                .padding(start = 8.dp) // etwas weiter nach links gerückt
        ) {
            Text(if (isRunning) "Stop" else "Start")
        }

        Text(
            text = time.toString(DurationUnit.SECONDS),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp) // Korrigiert von left und right zu start und end
                .requiredWidth(80.dp) // Mindestbreite des Textfelds
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VorschauStoppuhrContent() {
    StoppuhrContent()
}
