package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.Composable.components.BaderegelQuiz
import com.example.myswimsmartdb.ui.Composable.components.SharedViewModel
import com.example.myswimsmartdb.db.entities.Baderegel
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.theme.Platinum
import com.example.myswimsmartdb.db.Reposetory.BaderegelRepository

@Composable
fun QuizScreen(navController: NavController, sharedViewModel: SharedViewModel, baderegelRepository: BaderegelRepository) {
    var selectedLevel by remember { mutableStateOf<Int?>(null) }
    var showQuiz by remember { mutableStateOf(false) }

    BasisScreen(navController = navController, sharedViewModel = sharedViewModel) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.theorie),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Baderegeln",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )

            if (selectedLevel == null || !showQuiz) {
                LevelSelectionScreen { level ->
                    selectedLevel = level
                    showQuiz = true
                }
            } else {
                BaderegelQuiz(
                    baderegelRepository = baderegelRepository,
                    selectedLevel = selectedLevel!!,
                    onQuizEnd = {
                        showQuiz = false
                        selectedLevel = null
                    }
                )
            }
        }
    }
}

@Composable
fun LevelSelectionScreen(onLevelSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Wählen Sie ein Level", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onLevelSelected(1) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Bronze")
            }
            Button(
                onClick = { onLevelSelected(2) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Silber")
            }
            Button(
                onClick = { onLevelSelected(3) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Gold")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    val navController = rememberNavController()
    val sharedViewModel = SharedViewModel()
    val baderegelRepository = BaderegelRepository(LocalContext.current)
    QuizScreen(navController = navController, sharedViewModel = sharedViewModel, baderegelRepository = baderegelRepository)
}
