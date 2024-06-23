package com.example.myswimsmartdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.Composable.AppNavigation
import com.example.myswimsmartdb.ui.theme.AppTheme
import com.example.myswimsmartdb.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

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

        Scaffold { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavigation(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }
}
