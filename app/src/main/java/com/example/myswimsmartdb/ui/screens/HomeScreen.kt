package com.example.myswimsmartdb.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.content.MainContent


@Composable
fun HomeScreen(navController: NavController) {
    BasisScreen(navController = navController) { innerPadding ->
        MainContent(innerPadding = innerPadding)
    }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreen(navController = rememberNavController())
}