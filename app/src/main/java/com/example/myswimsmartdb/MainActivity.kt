package com.example.myswimsmartdb

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.Composable.AppNavigation
import com.example.myswimsmartdb.ui.Composable.components.SharedViewModel
import com.example.myswimsmartdb.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sharedViewModel = SharedViewModel()
            MyApp(sharedViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(sharedViewModel: SharedViewModel) {
    AppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current

        // Define a launcher to request the permission
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    // Permission granted, proceed with your functionality
                } else {
                    // Permission denied, show a message to the user
                }
            }
        )

        // Check and request permission
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavigation(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }
}
