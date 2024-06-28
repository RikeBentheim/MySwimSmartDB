package com.example.myswimsmartdb.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.content.BahnenzaehlenContent
import com.example.myswimsmartdb.ui.theme.Platinum

@Composable
fun BahnenschwimmenScreen(navController: NavHostController) {
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Handle back press
    BackHandler {
        showConfirmationDialog = true
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(text = "Bestätigung erforderlich") },
            text = { Text(text = "Möchten Sie die Seite wirklich verlassen?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmationDialog = false
                    navController.popBackStack()
                }) {
                    Text("Verlassen")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header-Bild
            Image(
                painter = painterResource(id = R.drawable.schwimmhalle),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Titeltext
            Text(
                text = stringResource(id = R.string.count_laps),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )
            BahnenzaehlenContent()
        }
    }
}
