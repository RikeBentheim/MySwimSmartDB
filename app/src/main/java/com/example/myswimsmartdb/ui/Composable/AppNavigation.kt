package com.example.myswimsmartdb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myswimsmartdb.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }
        composable("stoppuhr") { StoppuhrScreen(navController) }
        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController) }
        composable("Schwimmkurs") { KursScreen(navController) }
    }
}
