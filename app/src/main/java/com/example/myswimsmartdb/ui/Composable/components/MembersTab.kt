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
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.db.DateConverter
import java.util.Date

// Hauptkomponente für die Mitgliedsübersicht
@Composable
fun MembersTab(kursId: Int, mitgliedRepository: MitgliedRepository) {
    val members = remember { mutableStateOf(emptyList<Mitglied>()) }
    var selectedMember by remember { mutableStateOf<Mitglied?>(null) }

    LaunchedEffect(kursId) {
        members.value = mitgliedRepository.getFullMitgliederDetailsByKursId(kursId)
    }

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
}

// Komponente für die Anzeige der Mitgliederdetails
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetail(member: Mitglied, onBack: () -> Unit) {
    var showTasks by remember { mutableStateOf(false) }
    var showAttendance by remember { mutableStateOf(false) }

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
    }
}
