package com.example.myswimsmartdb.ui.Composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myswimsmartdb.ui.Composable.components.SharedViewModel
import com.example.myswimsmartdb.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val selectedCourse by sharedViewModel.selectedCourse.collectAsState()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }

        composable("stoppuhr") {
            StoppuhrScreen(navController, null, sharedViewModel)
        }

        composable("stoppuhr/{mitgliedIds}") { backStackEntry ->
            val mitgliedIds = backStackEntry.arguments?.getString("mitgliedIds")?.split(",")?.map { it.toInt() }
            StoppuhrScreen(navController = navController, mitgliedIds = mitgliedIds, sharedViewModel = sharedViewModel)
        }

        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController) }
        composable("kursBearbeiten") { KursScreen(navController) }
        composable("kursVerwaltung") { KursVerwaltungScreen(navController, sharedViewModel) }

        composable(
            "kursVerwaltungBack/{selectedCourse}/{selectedDate}",
            arguments = listOf(
                navArgument("selectedCourse") { type = NavType.StringType },
                navArgument("selectedDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            KursVerwaltungScreen(navController, sharedViewModel, selectedCourse, selectedDate)
        }
    }
}
