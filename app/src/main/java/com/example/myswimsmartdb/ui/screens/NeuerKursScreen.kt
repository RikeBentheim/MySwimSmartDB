package com.example.myswimsmartdb.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

    var showAddMember by remember { mutableStateOf(false) }
    var newKursId by remember { mutableStateOf(0L) }

    BasisScreen(navController = navController) { innerPadding ->
        AddKursScreen(
            kursRepository = KursRepository(context),
            levelRepository = LevelRepository(context),
            mitgliedRepository = MitgliedRepository(context),
            trainingRepository = TrainingRepository(context),
            showAddMember = showAddMember,
            setShowAddMember = { showAddMember = it },
            newKursId = newKursId,
            setNewKursId = { newKursId = it }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NeuerKursScreenPreview() {
    NeuerKursScreen(navController = rememberNavController())
}
