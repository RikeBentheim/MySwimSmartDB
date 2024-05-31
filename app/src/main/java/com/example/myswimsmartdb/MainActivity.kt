package com.example.myswimsmartdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myswimsmartdb.ui.theme.AppTheme
import com.example.myswimsmartdb.ui.Composable.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    AppTheme {
        val navController = rememberNavController()
        Scaffold {
            AppNavHost(navController = navController)
        }
    }
}
