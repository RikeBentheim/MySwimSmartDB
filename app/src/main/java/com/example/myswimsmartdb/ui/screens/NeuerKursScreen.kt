package com.example.myswimsmartdb.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.db.KursRepository
import com.example.myswimsmartdb.db.LevelRepository
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.db.TrainingRepository
import com.example.myswimsmartdb.ui.Composable.AddKursScreen

@Composable
fun NeuerKursScreen(navController: NavController) {
    val context = LocalContext.current

    BasisScreen(navController = navController) { innerPadding ->
        AddKursScreen(
            kursRepository = KursRepository(context),
            levelRepository = LevelRepository(context),
            mitgliedRepository = MitgliedRepository(context),
            trainingRepository = TrainingRepository(context)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NeuerKursScreenPreview() {
    NeuerKursScreen(navController = rememberNavController())
}
