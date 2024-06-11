package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.db.DateConverter
import com.example.myswimsmartdb.db.MitgliedRepository
import java.util.Date
import java.util.concurrent.TimeUnit
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Party

import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersTab(kursId: Int, mitgliedRepository: MitgliedRepository){
    var showTasks by remember { mutableStateOf(false) }
    var showAttendance by remember { mutableStateOf(false) }
    val allTasksCompleted = member.aufgaben.all { it.erledigt }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Mitglied:", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Vorname: ${member.vorname}")
        Text(text = "Nachname: ${member.nachname}")
        Text(text = "Geburtsdatum: ${member.geburtsdatumString}")
        Text(text = "Telefon: ${member.telefon}")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aufgaben",
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
            text = "Anwesenheit",
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
                        // Hier wird die Textfarbe basierend auf dem Datum geändert
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
            Text(text = "Zurück")
        }

        if (allTasksCompleted) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        position = Position.Relative(0.5, 0.3),
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                    )
                )
            )
        }
    }
}
