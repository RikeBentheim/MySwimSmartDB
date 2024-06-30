package com.example.myswimsmartdb.ui.Composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.example.myswimsmartdb.db.entities.Training
import com.example.myswimsmartdb.ui.screens.*
import com.example.myswimsmartdb.ui.viewmodel.SharedViewModel
import com.example.myswimsmartdb.ui.screens.KursVerwaltungScreen

@Composable
fun AppNavigation(navController: NavHostController, sharedViewModel: SharedViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }

        composable("stoppuhr") {
            StoppuhrScreen(navController, null, sharedViewModel)
        }

        composable("stoppuhr/{mitgliedIds}/{training}",
            arguments = listOf(
                navArgument("mitgliedIds") { type = NavType.StringType },
                navArgument("training") { type = NavType.ParcelableType(Training::class.java) }
            )) { backStackEntry ->
            val mitgliedIds = backStackEntry.arguments?.getString("mitgliedIds")?.split(",")?.map { it.toInt() }
            val training = backStackEntry.arguments?.getParcelable<Training>("training")
            sharedViewModel.selectedTraining = training
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
            val selectedCourse = sharedViewModel.selectedCourse
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            KursVerwaltungScreen(navController, sharedViewModel, selectedCourse, selectedDate)
        }
    }
}
