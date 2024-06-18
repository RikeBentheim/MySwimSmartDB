package com.example.myswimsmartdb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myswimsmartdb.ui.Composable.components.MitgliedAufgabeTab
import com.example.myswimsmartdb.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }

        // Route ohne mitgliedIds
        composable("stoppuhr") {
            StoppuhrScreen(navController, null)
        }

        // Route mit mitgliedIds
        composable("stoppuhr/{mitgliedIds}",
            arguments = listOf(navArgument("mitgliedIds") { defaultValue = "" })) { backStackEntry ->
            val mitgliedIdsString = backStackEntry.arguments?.getString("mitgliedIds") ?: ""
            val mitgliedIds = mitgliedIdsString.split(",").filter { it.isNotEmpty() }.map { it.toInt() }
            StoppuhrScreen(navController, mitgliedIds)
        }

        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController) }
        composable("kursBearbeiten") { KursScreen(navController) }
        composable("kursVerwaltung") { KursVerwaltungScreen(navController) }
    }
}
