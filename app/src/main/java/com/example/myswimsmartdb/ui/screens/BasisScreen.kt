package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.Composable.DrawerContent
import com.example.myswimsmartdb.ui.Composable.components.BackgroundImage
import com.example.myswimsmartdb.ui.Composable.components.CustomBottomBar
import com.example.myswimsmartdb.ui.Composable.components.CustomTopAppBar
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasisScreen(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Hintergrundbild
            BackgroundImage()

            Scaffold(
                topBar = { CustomTopAppBar(drawerState, scope, "SmartSwimm") },
                bottomBar = { CustomBottomBar() },
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ) { innerPadding ->
                content(innerPadding)
            }
        }
    }
}


