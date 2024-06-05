package com.example.myswimsmartdb.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.Composable.AddMemberScreen
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitgliederManagement(
    kursId: Int,
    mitgliedRepository: MitgliedRepository,
    selectedLevel: Level
) {
    var selectedMitglied by remember { mutableStateOf<Mitglied?>(null) }
    val mitglieder by remember { mutableStateOf(mitgliedRepository.getMitgliederByKursId(kursId)) }
    var showAddMemberScreen by remember { mutableStateOf(false) }
    val mitgliedNamen = mitglieder.map { "${it.vorname} ${it.nachname}" }

    if (showAddMemberScreen) {
        AddMemberScreen(
            kursId = kursId,
            selectedLevel = selectedLevel,
            mitgliedRepository = mitgliedRepository,
            onFinish = {
                showAddMemberScreen = false
                selectedMitglied = null
            },
            existingMitglied = selectedMitglied
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Training Management", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            StringSelectionDropdown(
                label = "Mitglied auswählen",
                options = mitgliedNamen,
                selectedOption = selectedMitglied?.let { "${it.vorname} ${it.nachname}" } ?: "",
                onOptionSelected = { selectedName ->
                    selectedMitglied = mitglieder.firstOrNull { "${it.vorname} ${it.nachname}" == selectedName }
                }
            )

            if (selectedMitglied != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            mitgliedRepository.deleteMitglied(selectedMitglied!!.id)
                            selectedMitglied = null
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("Mitglied löschen")
                    }
                    Button(
                        onClick = {
                            showAddMemberScreen = true
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("Mitglied bearbeiten")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    showAddMemberScreen = true
                    selectedMitglied = null
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Neues Mitglied hinzufügen")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MitgliederManagementPreview() {
    val context = LocalContext.current
    val dummyLevel = Level(id = 1, name = "Bronze", aufgaben = listOf())
    val dummyMitgliedRepository = MitgliedRepository(context)

    MitgliederManagement(
        kursId = 1,
        mitgliedRepository = dummyMitgliedRepository,
        selectedLevel = dummyLevel
    )
}
