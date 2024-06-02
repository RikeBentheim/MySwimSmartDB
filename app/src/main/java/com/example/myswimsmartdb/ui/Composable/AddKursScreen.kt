package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.LevelRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level

@Composable
fun AddKursScreen(
    navController: NavController,
    kursRepository: KursRepository,
    levelRepository: LevelRepository
) {
    var kursName by remember { mutableStateOf("") }
    val levels = levelRepository.getAllLevels()
    var selectedLevel by remember { mutableStateOf<Level?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = kursName,
            onValueChange = { kursName = it },
            label = { Text("Kurs Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        StringSelectionDropdown(
            label = "Level",
            options = levels.map { it.name },
            selectedOption = selectedLevel?.name ?: "",
            onOptionSelected = { selectedLevel = levels.find { level -> level.name == it } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                selectedLevel?.let { level ->
                    val kurs = Kurs(
                        id = 0,
                        name = kursName,
                        levelId = level.id,
                        levelName = level.name,
                        mitglieder = emptyList(),
                        trainings = emptyList(),
                        aufgaben = emptyList()
                    )
                    kursRepository.insertKursWithDetails(kurs)
                    // Optionale Benachrichtigung über das erfolgreiche Speichern
                    // oder andere Aktionen, die an dieser Stelle gewünscht sind
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Speichern")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddKursScreenPreview() {
    AddKursScreen(
        navController = rememberNavController(),
        kursRepository = KursRepository(LocalContext.current),
        levelRepository = LevelRepository(LocalContext.current)
    )
}
