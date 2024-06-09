package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.Composable.BasisScreen
import com.example.myswimsmartdb.ui.Composable.components.AttendanceTab
import com.example.myswimsmartdb.ui.Composable.components.MembersTab
import com.example.myswimsmartdb.ui.Composable.components.MitgliedAufgabeTab
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

    val courses = kursRepository.getAllKurseWithDetails()
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    val showDetails = remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val currentDate = sdf.format(Date())

    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Schwimmverein Haltern",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!showDetails.value) {
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

                if (selectedDate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { showDetails.value = true }) {
                        Text("Kurs starten")
                    }
                }
            } else {
                selectedCourse?.let { course ->
                    val selectedTraining = course.trainings.find { it.datum == selectedDate }
                    if (selectedTraining != null) {
                        KursDetails(course, selectedTraining.id, selectedDate, trainingRepository, mitgliedRepository, navController)
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
    mitgliedRepository: MitgliedRepository,
    navController: NavHostController
) {
    var selectedTab by remember { mutableStateOf(0) } // Set default tab to "Anwesenheit"
    val tabTitles = listOf("Anwesenheit", "Aufgaben", "Kursmitglieder")
    var selectedTask by remember { mutableStateOf<Aufgabe?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Kurs: ${course.name}", style = MaterialTheme.typography.headlineSmall, color = LapisLazuli)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Trainingsdatum: $trainingsDatum", style = MaterialTheme.typography.bodyMedium, color = LapisLazuli)
        Spacer(modifier = Modifier.height(16.dp))

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

        when (selectedTab) {
            0 -> AttendanceTab(course.id, trainingId)
            1 -> {
                if (selectedTask == null) {
                    TasksTab(
                        levelId = course.levelId,
                        kursId = course.id,
                        navController = navController,
                        onTaskSelected = { task -> selectedTask = task }
                    )
                } else {
                    MitgliedAufgabeTab(
                        taskId = selectedTask!!.id,
                        kursId = course.id,
                        onBackToTasks = { selectedTask = null },
                        navController = navController,
                        mitgliedRepository = mitgliedRepository
                    )
                }
            }
            2 -> MembersTab()
        }
    }
}
