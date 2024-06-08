package com.example.myswimsmartdb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myswimsmartdb.db.MitgliedRepository
import com.example.myswimsmartdb.ui.Composable.components.MitgliedAufgabeTab
import com.example.myswimsmartdb.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }
        composable("stoppuhr") { StoppuhrScreen(navController) }
        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController) }
        composable("kursBearbeiten") { KursScreen(navController) }
        composable("kursVerwaltung") { KursVerwaltungScreen(navController) }
        composable("mitgliedAufgabeTab/{taskId}/{kursId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: return@composable
            val kursId = backStackEntry.arguments?.getString("kursId")?.toInt() ?: return@composable
            MitgliedAufgabeTab(taskId, kursId, mitgliedRepository = MitgliedRepository(navController.context), onBackToTasks = { navController.popBackStack() }, navController = navController)
        }
    }
}
