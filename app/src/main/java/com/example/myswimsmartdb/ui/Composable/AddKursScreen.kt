package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.LevelRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKursScreen(
    kursRepository: KursRepository,
    levelRepository: LevelRepository,
    mitgliedRepository: MitgliedRepository,
    trainingRepository: TrainingRepository
) {
    var kursName by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var message by remember { mutableStateOf("") }
    var showAddMember by remember { mutableStateOf(false) }
    var newKursId by remember { mutableStateOf(0L) }

    val levels = levelRepository.getAllLevels()
    val levelNames = levels.map { it.name }

    if (showAddMember && newKursId != 0L) {
        AddMemberScreen(
            kursId = newKursId.toInt(),
            selectedLevel = selectedLevel!!,
            mitgliedRepository = mitgliedRepository,
            onFinish = {
                showAddMember = false
                message = "Mitglieder erfolgreich hinzugefügt"
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Neuen Kurs hinzufügen")

            OutlinedTextField(
                value = kursName,
                onValueChange = { kursName = it },
                label = { Text("Kurs Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            StringSelectionDropdown(
                label = "Level",
                options = levelNames,
                selectedOption = selectedLevel?.name ?: "Level auswählen",
                onOptionSelected = { selectedName ->
                    selectedLevel = levels.find { it.name == selectedName }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    val kurs = Kurs(
                        id = 0, // Die ID wird von der Datenbank automatisch generiert
                        name = kursName,
                        levelId = selectedLevel?.id ?: 0,
                        levelName = selectedLevel?.name ?: "",
                        mitglieder = emptyList(),
                        trainings = emptyList(),
                        aufgaben = emptyList()
                    )
                    newKursId = kursRepository.insertKursWithDetails(kurs)
                    message = if (newKursId != -1L) {
                        showAddMember = true
                        "Kurs erfolgreich hinzugefügt"
                    } else {
                        "Fehler beim Hinzufügen des Kurses"
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp),
                enabled = selectedLevel != null
            ) {
                Text(text = "Kurs hinzufügen")
            }

            if (message.isNotEmpty()) {
                Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Adding the new composable for adding kurstermine
            AddKurstermineScreen(
                kursId = newKursId.toInt(),
                trainingRepository = trainingRepository,
                mitgliedRepository = mitgliedRepository,
                onFinish = {
                    message = "Termine erfolgreich hinzugefügt"
                }
            )
        }
    }
}