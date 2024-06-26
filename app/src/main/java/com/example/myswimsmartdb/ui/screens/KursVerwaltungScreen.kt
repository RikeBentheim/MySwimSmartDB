package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.Composable.components.KursSaver
import com.example.myswimsmartdb.db.Reposetory.KursRepository
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.Reposetory.StoppuhrRepository
import com.example.myswimsmartdb.db.Reposetory.TrainingRepository
import com.example.myswimsmartdb.db.entities.Aufgabe
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.Composable.components.AttendanceTab
import com.example.myswimsmartdb.ui.Composable.components.MembersTab
import com.example.myswimsmartdb.ui.Composable.components.MitgliedAufgabeTab
import com.example.myswimsmartdb.ui.Composable.components.TasksTab
import com.example.myswimsmartdb.ui.theme.Platinum
import com.example.myswimsmartdb.ui.theme.SkyBlue
import com.example.myswimsmartdb.ui.theme.LapisLazuli
import com.example.myswimsmartdb.ui.viewmodel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KursVerwaltungScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    selectedCourse: Kurs? = null,
    selectedDate: String = ""
) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)
    val stoppuhrRepository = StoppuhrRepository(context)
    val coroutineScope = rememberCoroutineScope()

    var courses by remember { mutableStateOf(emptyList<Kurs>()) }
    var selectedCourseState by rememberSaveable(stateSaver = KursSaver) { mutableStateOf<Kurs?>(selectedCourse) }
    var selectedDateState by rememberSaveable { mutableStateOf(selectedDate) }
    val showDetails = rememberSaveable { mutableStateOf(selectedCourse != null && selectedDate.isNotEmpty()) }

    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val currentDate = sdf.format(Date())

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            courses = kursRepository.getAllKurseWithDetails()
        }
    }

    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.adobestock_288862937),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.training),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (!showDetails.value) {
                StringSelectionDropdown(
                    label = stringResource(id = R.string.bitte_kurs_auswaehlen),
                    options = courses.map { it.name },
                    selectedOption = selectedCourseState?.name ?: "",
                    onOptionSelected = { courseName ->
                        selectedCourseState = courses.find { it.name == courseName }
                        val trainingDates = selectedCourseState?.trainings?.map { it.datum }
                        if (currentDate in trainingDates.orEmpty()) {
                            selectedDateState = currentDate
                        } else {
                            selectedDateState = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                selectedCourseState?.let { course ->
                    val trainingDates = course.trainings.map { it.datum }
                    if (trainingDates.isNotEmpty()) {
                        StringSelectionDropdown(
                            label = stringResource(id = R.string.training_datum_auswaehlen),
                            options = trainingDates,
                            selectedOption = selectedDateState,
                            onOptionSelected = { selectedDateState = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (selectedDateState.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        if (selectedCourseState != null) {
                            sharedViewModel.selectCourse(selectedCourseState!!)
                            val selectedTraining = selectedCourseState?.trainings?.find { it.datum == selectedDateState }
                            if (selectedTraining != null) {
                                sharedViewModel.selectTraining(selectedTraining)
                            }
                            showDetails.value = true
                        }
                    }) {
                        Text(stringResource(id = R.string.kurs_starten))
                    }
                }
            } else {
                selectedCourseState?.let { course ->
                    val selectedTraining = course.trainings.find { it.datum == selectedDateState }
                    if (selectedTraining != null) {
                        KursDetails(course, selectedTraining.id, selectedDateState, trainingRepository, mitgliedRepository, stoppuhrRepository, navController, sharedViewModel)
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
    stoppuhrRepository: StoppuhrRepository,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        stringResource(id = R.string.anwesenheit),
        stringResource(id = R.string.aufgaben),
        stringResource(id = R.string.kursmitglieder)
    )
    var selectedTask by remember { mutableStateOf<Aufgabe?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = stringResource(id = R.string.kurs, course.name), style = MaterialTheme.typography.headlineSmall, color = LapisLazuli)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.trainingsdatum, trainingsDatum), style = MaterialTheme.typography.bodyMedium, color = LapisLazuli)
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
                    TasksTab(levelId = course.levelId, kursId = course.id, onTaskSelected = { task ->
                        selectedTask = task
                    }, navController = navController, sharedViewModel = sharedViewModel)
                } else {
                    MitgliedAufgabeTab(
                        taskId = selectedTask!!.id,
                        kursId = course.id,
                        mitgliedRepository = mitgliedRepository,
                        onBackToTasks = { selectedTask = null }
                    )
                }
            }
            2 -> MembersTab(course.id, mitgliedRepository, stoppuhrRepository)
        }
    }
}
