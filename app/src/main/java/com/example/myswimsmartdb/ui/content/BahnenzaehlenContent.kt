package com.example.myswimsmartdb.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.entities.Bahnenzaehlen
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.screens.ProcessImageContent
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BahnenzaehlenContent() {
    MitgliederVerwaltung()
    ProcessImageContent()
}

@Composable
fun MitgliederVerwaltung(innerPadding: PaddingValues = PaddingValues()) {
    val bahnenzaehlen = remember { mutableStateListOf<Bahnenzaehlen>() }
    var vorname by remember { mutableStateOf("") }
    var nachname by remember { mutableStateOf("") }
    var idCounter by remember { mutableStateOf(1) }
    var selectedTimeOption by remember { mutableStateOf("Offen") }
    var selectedLaneLength by remember { mutableStateOf(25) }
    val timeOptions = listOf(
        stringResource(id = R.string.minutes_15),
        stringResource(id = R.string.minutes_20),
        stringResource(id = R.string.minutes_30),
        stringResource(id = R.string.open)
    )
    val laneLengthOptions = listOf(10, 25, 50)

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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
                        label = { Text(stringResource(id = R.string.first_name)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedTextField(
                        value = nachname,
                        onValueChange = { nachname = it },
                        label = { Text(stringResource(id = R.string.last_name)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = {
                        if (vorname.isNotBlank() && nachname.isNotBlank()) {
                            bahnenzaehlen.add(Bahnenzaehlen(idCounter++, 0, vorname, nachname, "", 0, selectedLaneLength, selectedTimeOption))
                            vorname = ""
                            nachname = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StringSelectionDropdown(
                        label = stringResource(id = R.string.select_time),
                        options = timeOptions,
                        selectedOption = selectedTimeOption,
                        onOptionSelected = { selectedTimeOption = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )

                    StringSelectionDropdown(
                        label = stringResource(id = R.string.select_lane_length),
                        options = laneLengthOptions.map { it.toString() },
                        selectedOption = selectedLaneLength.toString(),
                        onOptionSelected = { selectedLaneLength = it.toInt() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                }
            }
        }

        itemsIndexed(bahnenzaehlen) { index, bahnen ->
            BahnenzaehlenMitTimer(bahnen, onDelete = { bahnenzaehlen.removeAt(index) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BahnenzaehlenMitTimer(bahnen: Bahnenzaehlen, onDelete: () -> Unit) {
    var isRunning by remember { mutableStateOf(bahnen.running) }
    var time by remember { mutableStateOf(bahnen.zeit.toDuration(DurationUnit.MILLISECONDS)) }
    var lapCount by remember { mutableStateOf(bahnen.bahnen) }
    var showDialog by remember { mutableStateOf(false) }
    var showLapDialog by remember { mutableStateOf(false) }
    var editedLapCount by remember { mutableStateOf(bahnen.bahnen.toString()) }
    val coroutineScope = rememberCoroutineScope()
    val openString = stringResource(id = R.string.open)

    LaunchedEffect(isRunning) {
        if (isRunning) {
            coroutineScope.launch {
                while (isRunning) {
                    delay(1000L)
                    if (bahnen.zeitMode == openString) {
                        time += 1.toDuration(DurationUnit.SECONDS)
                    } else {
                        time -= 1.toDuration(DurationUnit.SECONDS)
                        if (time.inWholeMilliseconds <= 0) {
                            isRunning = false
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.options)) },
            text = {
                Column {
                    Button(
                        onClick = {
                            bahnen.reset()
                            time = bahnen.zeit.toDuration(DurationUnit.MILLISECONDS)
                            lapCount = bahnen.bahnen
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(stringResource(id = R.string.reset_timer))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onDelete()
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(stringResource(id = R.string.delete_member))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showLapDialog = true
                            editedLapCount = bahnen.bahnen.toString()
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(stringResource(id = R.string.change_laps))
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showLapDialog) {
        AlertDialog(
            onDismissRequest = { showLapDialog = false },
            title = { Text(stringResource(id = R.string.change_laps)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedLapCount,
                        onValueChange = { editedLapCount = it },
                        label = { Text(stringResource(id = R.string.laps)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val newLapCount = editedLapCount.toIntOrNull()
                            if (newLapCount != null) {
                                lapCount = newLapCount
                                bahnen.bahnen = newLapCount
                                showLapDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(stringResource(id = R.string.save_changes))
                    }
                }
            },
            confirmButton = {}
        )
    }

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
                .padding(vertical = 8.dp)
                .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(SkyBlue)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text("⟳", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${bahnen.vorname} ${bahnen.nachname}")
                Text("${stringResource(id = R.string.meters_swum)}: ${bahnen.getTotalMeters()}")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (isRunning) {
                        bahnen.stop()
                    } else {
                        bahnen.start()
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

            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .requiredWidth(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val hours = (time.inWholeSeconds / 3600).toString().padStart(2, '0')
                val minutes = ((time.inWholeSeconds % 3600) / 60).toString().padStart(2, '0')
                val seconds = (time.inWholeSeconds % 60).toString().padStart(2, '0')

                Text(
                    text = "$hours:$minutes:$seconds",
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.laps)}: $lapCount",
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(SkyBlue)
                    .clickable {
                        lapCount++
                        bahnen.addBahnen()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add), tint = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VorschauBahnenzaehlenContent() {
    BahnenzaehlenContent()
}
