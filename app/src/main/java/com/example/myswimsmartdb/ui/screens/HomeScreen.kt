package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.Composable.components.SharedViewModel
import com.example.myswimsmartdb.ui.content.MainContent

@Composable
fun HomeScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    BasisScreen(navController = navController, sharedViewModel = sharedViewModel) { innerPadding ->
        MainContent(innerPadding = innerPadding)
    }
}

@Preview
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    val sharedViewModel = SharedViewModel()
    HomeScreen(navController = navController, sharedViewModel = sharedViewModel)
}
