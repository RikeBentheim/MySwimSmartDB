package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.LevelRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.ui.Composable.AddKursScreen
import com.example.myswimsmartdb.ui.Composable.AddMemberScreen
import com.example.myswimsmartdb.ui.Composable.AddKurstermineScreen
import com.example.myswimsmartdb.ui.Composable.BasisScreen
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.ui.theme.Platinum
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NeuerKursScreen(navController: NavHostController) {
    // Initialisieren der benötigten Repositories
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val levelRepository = LevelRepository(context)
    val mitgliedRepository = MitgliedRepository(context)
    val trainingRepository = TrainingRepository(context)

    // State-Variablen zur Steuerung der Schritte und Speicherung von Eingabedaten
    var currentStep by remember { mutableStateOf(1) }
    var kursName by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var kursId by remember { mutableStateOf<Int?>(null) }

    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header-Bild
            Image(
                painter = painterResource(id = R.drawable.adobestock_288862937),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Titeltext
            Text(
                text = stringResource(id = R.string.neuer_kurs),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )
            Spacer(modifier = Modifier.height(30.dp))

            // Steuerung der Schritte
            when (currentStep) {
                1 -> AddKursScreen(
                    navController = navController,
                    kursRepository = kursRepository,
                    levelRepository = levelRepository,
                    onKursSaved = { name, level, newKursId ->
                        // Speichern der Kursinformationen und Wechsel zum nächsten Schritt
                        kursName = name
                        selectedLevel = level
                        kursId = newKursId
                        currentStep = 2
                    }
                )
                2 -> kursId?.let { id ->
                    AddMemberScreen(
                        kursId = id,
                        selectedLevel = selectedLevel!!,
                        mitgliedRepository = mitgliedRepository,
                        onFinish = {
                            // Wechsel zum nächsten Schritt nach Hinzufügen der Mitglieder
                            currentStep = 3
                        }
                    )
                }
                3 -> kursId?.let { id ->
                    AddKurstermineScreen(
                        kursId = id,
                        trainingRepository = trainingRepository,
                        mitgliedRepository = mitgliedRepository,
                        onFinish = {
                            // Wechsel zum nächsten Schritt nach Hinzufügen der Kurstermine
                            currentStep = 4
                        }
                    )
                }
                4 -> Text(text = stringResource(id = R.string.kurs_erfolgreich_erstellt))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NeuerKursScreenPreview() {
    NeuerKursScreen(navController = rememberNavController())
}
