package com.example.myswimsmartdb.ui.Composable

import com.example.myswimsmartdb.ui.screens.BahnenschwimmenScreen
import com.example.myswimsmartdb.ui.screens.StoppuhrScreen
import com.example.myswimsmartdb.ui.screens.TrainingScreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myswimsmartdb.ui.screens.HomeScreen
import com.example.myswimsmartdb.ui.screens.NeuerKursScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController = navController) }
        composable("neuerKurs") { NeuerKursScreen(navController = navController) }
        composable("home") { HomeScreen(navController) }
        composable("training") { TrainingScreen(navController) }
        composable("neuerKurs") { NeuerKursScreen(navController) }
        composable("stoppuhr") { StoppuhrScreen(navController) }
        composable("bahnenschwimmen") { BahnenschwimmenScreen(navController) }
        composable("schwimmkurs") { com.example.myswimsmartdb.ui.screens.KursScreen(navController) }

    }
}
