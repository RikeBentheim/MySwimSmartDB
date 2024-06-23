package com.example.myswimsmartdb.ui.Composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myswimsmartdb.ui.screens.*
import com.example.myswimsmartdb.ui.viewmodel.SharedViewModel

@Composable
fun AppNavigation(navController: NavHostController, sharedViewModel: SharedViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }

        composable("stoppuhr") {
            StoppuhrScreen(navController, null, sharedViewModel)
        }

        composable("stoppuhr/{mitgliedIds}",
            arguments = listOf(navArgument("mitgliedIds") { defaultValue = "" })) { backStackEntry ->
            val mitgliedIdsString = backStackEntry.arguments?.getString("mitgliedIds") ?: ""
            val mitgliedIds = mitgliedIdsString.split(",").filter { it.isNotEmpty() }.map { it.toInt() }
            StoppuhrScreen(navController, mitgliedIds, sharedViewModel)
        }

        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController) }
        composable("kursBearbeiten") { KursScreen(navController) }
        composable("kursVerwaltung") { KursVerwaltungScreen(navController, sharedViewModel) }
    }
}
