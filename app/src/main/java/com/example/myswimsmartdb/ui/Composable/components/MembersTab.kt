package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.db.DateConverter
import java.util.Date
import java.util.concurrent.TimeUnit
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersTab(kursId: Int, mitgliedRepository: MitgliedRepository) {
    val members = remember { mutableStateOf(emptyList<Mitglied>()) }
    var selectedMember by remember { mutableStateOf<Mitglied?>(null) }

    LaunchedEffect(kursId) {
        members.value = mitgliedRepository.getFullMitgliederDetailsByKursId(kursId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedMember == null) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(members.value) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMember = member }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${member.vorname} ${member.nachname}")
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = stringResource(id = R.string.show_details),
                            modifier = Modifier.clickable { selectedMember = member }
                        )
                    }
                }
            }
        } else {
            MemberDetail(
                member = selectedMember!!,
                onBack = { selectedMember = null }
            )
        }

        val showConfetti = remember { mutableStateOf(false) }

        if (selectedMember != null && selectedMember!!.aufgaben.all { it.erledigt }) {
            showConfetti.value = true
        }

        if (showConfetti.value) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        colors = listOf(0x8ecae6, 0x219ebc, 0x023047, 0xffb703, 0xfb8500),
                        angle = 0,
                        spread = 360,
                        speed = 1f,
                        maxSpeed = 10f,
                        fadeOutEnabled = true,
                        timeToLive = 2000L,
                        shapes = listOf(Shape.Square, Shape.Circle),
                        size = listOf(Size(12)),
                        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
                        emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(300)
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetail(member: Mitglied, onBack: () -> Unit) {
    var showTasks by remember { mutableStateOf(false) }
    var showAttendance by remember { mutableStateOf(false) }
    val allTasksCompleted = member.aufgaben.all { it.erledigt }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = stringResource(id = R.string.member), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${stringResource(id = R.string.vorname)}: ${member.vorname}")
        Text(text = "${stringResource(id = R.string.nachname)}: ${member.nachname}")
        Text(text = "${stringResource(id = R.string.geburtsdatum)}: ${member.geburtsdatumString}")
        Text(text = "${stringResource(id = R.string.telefon)}: ${member.telefon}")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.aufgaben),
            modifier = Modifier.clickable { showTasks = !showTasks },
            style = MaterialTheme.typography.headlineSmall
        )

        if (showTasks) {
            Column {
                member.aufgaben.forEach { aufgabe ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = aufgabe.aufgabe, modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = aufgabe.erledigt,
                            onCheckedChange = { /* Handle checkbox change */ }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.anwesenheit),
            modifier = Modifier.clickable { showAttendance = !showAttendance },
            style = MaterialTheme.typography.headlineSmall
        )

        if (showAttendance) {
            Column {
                member.anwesenheiten.forEach { anwesenheit ->
                    val trainingDate = DateConverter.stringToDate(anwesenheit.trainingDatum)
                    val isFutureDate = trainingDate?.after(Date()) ?: false
                    val textColor = if (isFutureDate) IndigoDye else SkyBlue

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = anwesenheit.trainingDatum,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = anwesenheit.anwesend,
                            onCheckedChange = { /* Handle checkbox change */ }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text(text = stringResource(id = R.string.back_to_tasks))
        }

        if (allTasksCompleted) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        colors = listOf(0x8ecae6, 0x219ebc, 0x023047, 0xffb703, 0xfb8500),
                        angle = 0,
                        spread = 360,
                        speed = 1f,
                        maxSpeed = 10f,
                        fadeOutEnabled = true,
                        timeToLive = 2000L,
                        shapes = listOf(Shape.Square, Shape.Circle),
                        size = listOf(Size(12)),
                        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
                        emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(300)
                    )
                )
            )
        }
    }
}
