
// AddMemberScreen.kt
package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.db.entities.Mitglied

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(kursId: Int, selectedLevel: Level, mitgliedRepository: MitgliedRepository, onFinish: () -> Unit) {
    var vorname by remember { mutableStateOf("") }
    var nachname by remember { mutableStateOf("") }
    var geburtsdatum by remember { mutableStateOf("") }
    var telefon by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val mitglieder = remember { mutableStateOf(mitgliedRepository.getMitgliederByKursId(kursId)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Kurs: ${selectedLevel.name}")

        Text(text = "Teilnehmer")

        mitglieder.value.forEach { mitglied ->
            Text(text = "${mitglied.vorname} ${mitglied.nachname}")
        }

        OutlinedTextField(
            value = vorname,
            onValueChange = { vorname = it },
            label = { Text("Vorname") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = nachname,
            onValueChange = { nachname = it },
            label = { Text("Nachname") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = geburtsdatum,
            onValueChange = { geburtsdatum = it },
            label = { Text("Geburtsdatum") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = telefon,
            onValueChange = { telefon = it },
            label = { Text("Telefon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
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
                    message = "Mitglied erfolgreich hinzugefügt"
                    mitglieder.value = mitgliedRepository.getMitgliederByKursId(kursId)
                    vorname = ""
                    nachname = ""
                    geburtsdatum = ""
                    telefon = ""
                } else {
                    message = "Fehler beim Hinzufügen des Mitglieds"
                }
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Mitglied hinzufügen")
        }

        Button(
            onClick = {
                onFinish()
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Eingabe beenden")
        }

        if (message.isNotEmpty()) {
            Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
