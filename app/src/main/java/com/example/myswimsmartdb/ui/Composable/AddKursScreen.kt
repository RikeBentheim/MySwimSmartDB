package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.clickable
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
    trainingRepository: TrainingRepository,
    showAddMember: Boolean,
    setShowAddMember: (Boolean) -> Unit,
    newKursId: Long,
    setNewKursId: (Long) -> Unit
) {
    var kursName by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val levels = levelRepository.getAllLevels()
    val levelNames = levels.map { it.name }

    if (showAddMember && newKursId != 0L) {
        AddMemberScreen(
            kursId = newKursId.toInt(),
            selectedLevel = selectedLevel!!,
            mitgliedRepository = mitgliedRepository,
            onFinish = {
                setShowAddMember(false)
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
                label = { Text("Kursname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = selectedLevel?.name ?: "Level auswählen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable (onClick = { expanded = true })
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    levelNames.forEach { levelName ->
                        DropdownMenuItem(
                            text = { Text(levelName) },
                            onClick = {
                                selectedLevel = levels.find { it.name == levelName }
                                expanded = false
                            }
                        )
                    }
                }
            }

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
                    val id = kursRepository.insertKursWithDetails(kurs)
                    setNewKursId(id)
                    message = if (id != -1L) {
                        setShowAddMember(true)
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
