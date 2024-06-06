package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.Composable.BasisScreen
import com.example.myswimsmartdb.ui.Composable.components.AttendanceTab
import com.example.myswimsmartdb.ui.Composable.components.MembersTab
import com.example.myswimsmartdb.ui.Composable.components.TasksTab
import com.example.myswimsmartdb.ui.theme.Platinum
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.ui.theme.LapisLazuli
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KursVerwaltungScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)

    // Liste der verfügbaren Kurse laden
    val courses = kursRepository.getAllKurseWithDetails()
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    val showDetails = remember { mutableStateOf(false) }

    // Aktuelles Datum im richtigen Format
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val currentDate = sdf.format(Date())

    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bild
            Image(
                painter = painterResource(id = R.drawable.adobestock_288862937),
                contentDescription = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Titeltext
            Text(
                text = stringResource(id = R.string.schwimmverein_haltern),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!showDetails.value) {
                // Dropdown-Menü zur Auswahl eines Kurses
                StringSelectionDropdown(
                    label = "Bitte einen Kurs auswählen:",
                    options = courses.map { it.name },
                    selectedOption = selectedCourse?.name ?: "",
                    onOptionSelected = { courseName ->
                        selectedCourse = courses.find { it.name == courseName }
                        val trainingDates = selectedCourse?.trainings?.map { it.datum }
                        if (currentDate in trainingDates.orEmpty()) {
                            selectedDate = currentDate
                        } else {
                            selectedDate = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dropdown-Menü zur Auswahl eines Training Datums, wenn ein Kurs ausgewählt wurde
                selectedCourse?.let { course ->
                    val trainingDates = course.trainings.map { it.datum }
                    if (trainingDates.isNotEmpty()) {
                        StringSelectionDropdown(
                            label = "Training Datum auswählen",
                            options = trainingDates,
                            selectedOption = selectedDate,
                            onOptionSelected = { selectedDate = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Button zum Starten des Kurses, wenn ein Datum ausgewählt ist
                if (selectedDate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { showDetails.value = true }) {
                        Text("Kurs starten")
                    }
                }
            } else {
                // Kursdetails anzeigen, wenn der Kurs gestartet wurde
                selectedCourse?.let { course ->
                    val selectedTraining = course.trainings.find { it.datum == selectedDate }
                    if (selectedTraining != null) {
                        KursDetails(course, selectedTraining.id, selectedDate, trainingRepository, mitgliedRepository)
                    }
                }
            }
        }
    }
}

@Composable
fun KursDetails(
    course: Kurs,
    trainingId: Int,
    trainingsDatum: String,
    trainingRepository: TrainingRepository,
    mitgliedRepository: MitgliedRepository
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Anwesenheit", "Aufgaben", "Kursmitglieder")

    Column(modifier = Modifier.padding(16.dp)) {
        // Zeige Kursnamen und Trainingsdatum an
        Text(text = "Kurs: ${course.name}", style = MaterialTheme.typography.headlineSmall, color = LapisLazuli)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Trainingsdatum: $trainingsDatum", style = MaterialTheme.typography.bodyMedium, color = LapisLazuli)
        Spacer(modifier = Modifier.height(16.dp))

        // Tabs zur Navigation zwischen verschiedenen Ansichten
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Platinum,
            contentColor = SkyBlue,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .background(LapisLazuli)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) LapisLazuli else SkyBlue,
                            style = if (selectedTab == index) MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline) else MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }

        // Inhalt basierend auf dem ausgewählten Tab anzeigen
        when (selectedTab) {
            0 -> AttendanceTab(course.id, trainingId)
            1 -> TasksTab()
            2 -> MembersTab()
        }
    }
}
