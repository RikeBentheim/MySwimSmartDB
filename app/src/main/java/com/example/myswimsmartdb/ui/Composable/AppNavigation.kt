package com.example.myswimsmartdb.ui.Composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myswimsmartdb.ui.Composable.components.SharedViewModel
import com.example.myswimsmartdb.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController, sharedViewModel: SharedViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, sharedViewModel) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController, sharedViewModel) }

        composable("stoppuhr") {
            StoppuhrScreen(navController, null, sharedViewModel)
        }

        composable("stoppuhr/{mitgliedIds}", arguments = listOf(navArgument("mitgliedIds") { type = NavType.StringType })) { backStackEntry ->
            val mitgliedIds = backStackEntry.arguments?.getString("mitgliedIds")?.split(",")?.map { it.toInt() }
            StoppuhrScreen(navController, mitgliedIds, sharedViewModel)
        }

        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController, sharedViewModel) }
        composable("kursBearbeiten") { KursScreen(navController, sharedViewModel) }
        composable("kursVerwaltung") { KursVerwaltungScreen(navController, sharedViewModel) }

        composable(
            "kursVerwaltungBack/{selectedCourse}/{selectedDate}",
            arguments = listOf(
                navArgument("selectedCourse") { type = NavType.StringType },
                navArgument("selectedDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val selectedCourse = sharedViewModel.selectedCourse
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            KursVerwaltungScreen(navController, sharedViewModel, selectedCourse, selectedDate)
        }
    }
}
