package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.layout.ui.layout.components.DatePickerButton
import com.example.myswimsmartdb.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    kursId: Int,
    selectedLevel: Level,
    mitgliedRepository: MitgliedRepository,
    onFinish: () -> Unit,
    existingMitglied: Mitglied? = null
) {
    var vorname by remember { mutableStateOf(existingMitglied?.vorname ?: "") }
    var nachname by remember { mutableStateOf(existingMitglied?.nachname ?: "") }
    var geburtsdatum by remember { mutableStateOf(existingMitglied?.geburtsdatumString ?: "") }
    var telefon by remember { mutableStateOf(existingMitglied?.telefon ?: "") }
    var message by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    var showInputFields by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var mitglieder by remember { mutableStateOf(listOf<Mitglied>()) }

    LaunchedEffect(kursId) {
        mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
    }

    val mitgliedHinzugefuegt = stringResource(id = R.string.mitglied_hinzugefuegt)
    val mitgliedHinzufuegenFehler = stringResource(id = R.string.mitglied_hinzufuegen_fehler)
    val mitgliedAktualisiert = stringResource(id = R.string.mitglied_aktualisiert)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.kurs, selectedLevel.name))
        if (showInputFields) {
            Text(text = stringResource(id = R.string.kursmitglieder))
            mitglieder.forEach { mitglied ->
                Text(text = "${mitglied.vorname} ${mitglied.nachname}")
            }
            OutlinedTextField(
                value = vorname,
                onValueChange = { vorname = it },
                label = { Text(stringResource(id = R.string.vorname)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = nachname,
                onValueChange = { nachname = it },
                label = { Text(stringResource(id = R.string.nachname)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            DatePickerButton(
                selectedDate = geburtsdatum,
                onDateSelected = { geburtsdatum = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = telefon,
                onValueChange = { telefon = it },
                label = { Text(stringResource(id = R.string.telefon)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (existingMitglied == null) {
                            // Neues Mitglied erstellen
                            val mitglied = Mitglied(
                                id = 0,
                                vorname = vorname,
                                nachname = nachname,
                                geburtsdatumString = geburtsdatum,
                                telefon = telefon,
                                kursId = kursId
                            )
                            val aufgaben = selectedLevel.aufgaben
                            val mitgliedId = mitgliedRepository.insertMitgliedWithAufgaben(mitglied, aufgaben)
                            if (mitgliedId != -1L) {
                                message = mitgliedHinzugefuegt
                                mitglieder = mitgliedRepository.getMitgliederByKursId(kursId)
                                vorname = ""
                                nachname = ""
                                geburtsdatum = ""
                                telefon = ""
                            } else {
                                message = mitgliedHinzufuegenFehler
                            }
                        } else {
                            // Bestehendes Mitglied aktualisieren
                            val updatedMitglied = existingMitglied.copy(
                                vorname = vorname,
                                nachname = nachname,
                                geburtsdatumString = geburtsdatum,
                                telefon = telefon
                            )
                            mitgliedRepository.updateMitglied(updatedMitglied)
                            message = mitgliedAktualisiert
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = if (existingMitglied == null) stringResource(id = R.string.mitglied_hinzufuegen) else stringResource(id = R.string.mitglied_aktualisieren))
            }
            Button(
                onClick = {
                    showInputFields = false
                    onFinish()
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.eingabe_beenden))
            }
            if (message.isNotEmpty()) {
                Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            mitglieder.forEach { mitglied ->
                Text(text = "${mitglied.vorname} ${mitglied.nachname}")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddMemberScreenPreview() {
    val context = LocalContext.current
    val dummyLevel = Level(id = 1, name = "Bronze", aufgaben = listOf())
    val dummyMitgliedRepository = MitgliedRepository(context)

    AddMemberScreen(
        kursId = 1,
        selectedLevel = dummyLevel,
        mitgliedRepository = dummyMitgliedRepository,
        onFinish = {}
    )
}
