package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.KursRepository
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.Reposetory.TrainingRepository
import com.example.myswimsmartdb.db.entities.Kurs
import com.example.myswimsmartdb.db.entities.Level
import com.example.myswimsmartdb.ui.Composable.StringSelectionDropdown
import com.example.myswimsmartdb.ui.content.CourseDetails
import com.example.myswimsmartdb.ui.content.MitgliederManagement
import com.example.myswimsmartdb.ui.content.TrainingManagement
import com.example.myswimsmartdb.ui.theme.Platinum
import com.example.myswimsmartdb.ui.theme.Cerulean
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KursScreen(navController: NavHostController) {
    val context = LocalContext.current
    val kursRepository = KursRepository(context)
    val trainingRepository = TrainingRepository(context)
    val mitgliedRepository = MitgliedRepository(context)
    val coroutineScope = rememberCoroutineScope()

    // Liste der verfügbaren Kurse laden
    var courses by remember { mutableStateOf(emptyList<Kurs>()) }
    var selectedCourse by remember { mutableStateOf<Kurs?>(null) }
    var editMode by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteMessage by remember { mutableStateOf("") }

    // Strings für den Dialog laden
    val deleteDialogTitle = stringResource(id = R.string.kurs_loeschen_titel)
    val deleteDialogText = stringResource(id = R.string.kurs_loeschen_text)
    val deleteButtonText = stringResource(id = R.string.kurs_loeschen)
    val cancelButtonText = stringResource(id = R.string.abbrechen)
    val deletedMessage = stringResource(id = R.string.kurs_geloescht)

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
            // Header Bild
            Image(
                painter = painterResource(id = R.drawable.adobestock_288862937),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Titeltext
            Text(
                text = stringResource(id = R.string.kurs_bearbeiten),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (!editMode) {
                // Dropdown-Menü zur Auswahl eines Kurses
                StringSelectionDropdown(
                    label = stringResource(id = R.string.bitte_kurs_auswaehlen),
                    options = courses.map { it.name },
                    selectedOption = selectedCourse?.name ?: "",
                    onOptionSelected = { courseName ->
                        selectedCourse = courses.find { it.name == courseName }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Kursdetails und Bearbeiten-Button anzeigen, wenn ein Kurs ausgewählt wurde
                selectedCourse?.let { course ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                editMode = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(stringResource(id = R.string.kurs_bearbeiten), color = Platinum)
                        }

                        Button(
                            onClick = {
                                showDeleteDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(stringResource(id = R.string.kurs_loeschen), color = Platinum)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Details des ausgewählten Kurses anzeigen
                    CourseDetails(
                        course = course,
                        trainingRepository = trainingRepository,
                        mitgliedRepository = mitgliedRepository
                    )
                }
            } else {
                // Trainings- und Mitgliedermanagement anzeigen, wenn im Bearbeiten-Modus
                selectedCourse?.let { course ->
                    Column {
                        TrainingManagement(
                            course = course,
                            trainingRepository = trainingRepository,
                            mitgliedRepository = mitgliedRepository,
                            onEndEditing = {
                                editMode = false
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        MitgliederManagement(
                            kursId = course.id,
                            mitgliedRepository = mitgliedRepository,
                            selectedLevel = Level(course.levelId, course.levelName, listOf())
                        )
                    }
                }
            }

            // Erfolgs- oder Fehlermeldung anzeigen
            if (deleteMessage.isNotEmpty()) {
                Text(
                    text = deleteMessage,
                    color = Cerulean,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Dialog zum Bestätigen des Löschens
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    selectedCourse?.let {
                                        kursRepository.deleteKursWithDetails(it.id)
                                        deleteMessage = deletedMessage
                                        selectedCourse = null
                                        courses = kursRepository.getAllKurseWithDetails()
                                    }
                                    showDeleteDialog = false
                                }
                            }
                        ) {
                            Text(deleteButtonText)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(cancelButtonText)
                        }
                    },
                    title = { Text(deleteDialogTitle) },
                    text = { Text(deleteDialogText) }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun KursScreenPreview() {
    KursScreen(navController = rememberNavController())
}
