package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.db.Reposetory.KursRepository
import com.example.myswimsmartdb.db.Reposetory.LevelRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.ui.theme.Platinum
import com.example.myswimsmartdb.ui.theme.IndigoDye
import com.example.myswimsmartdb.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKursScreen(
    navController: NavController,
    kursRepository: KursRepository,
    levelRepository: LevelRepository,
    onKursSaved: (String, Level, Int) -> Unit
) {
    var kursName by remember { mutableStateOf("") }
    val levels = levelRepository.getAllLevels()
    var selectedLevel by remember { mutableStateOf<Level?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = kursName,
            onValueChange = { kursName = it },
            label = { Text(stringResource(id = R.string.kurs_name), color = IndigoDye) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),

        )
        Spacer(modifier = Modifier.height(20.dp))

        StringSelectionDropdown(
            label = stringResource(id = R.string.level),
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
                    val newKursId = kursRepository.insertKursWithDetails(kurs).toInt()
                    onKursSaved(kursName, level, newKursId)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.speichern))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddKursScreenPreview() {
    val context = LocalContext.current
    val dummyKursRepository = KursRepository(context)
    val dummyLevelRepository = LevelRepository(context)
    AddKursScreen(
        navController = rememberNavController(),
        kursRepository = dummyKursRepository,
        levelRepository = dummyLevelRepository,
        onKursSaved = { _, _, _ -> }
    )
}
