package com.example.myswimsmartdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.db.Reposetory.MitgliedRepository
import com.example.myswimsmartdb.db.entities.Mitglied
import com.example.myswimsmartdb.ui.Composable.components.MitgliederStoppuhrVerwaltung
import com.example.myswimsmartdb.ui.Composable.components.MitgliederVerwaltung
import com.example.myswimsmartdb.ui.theme.Platinum

@Composable
fun StoppuhrScreen(navController: NavHostController, mitgliedIds: List<Int>?) {
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
                text = stringResource(id = R.string.stoppuhr),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp),
                color = Platinum
            )
            if (mitgliedIds == null) {
                MitgliederVerwaltung()
            } else {
                val context = LocalContext.current
                val mitgliedRepository = MitgliedRepository(context)
                var mitglieder by remember { mutableStateOf(listOf<Mitglied>()) }

                LaunchedEffect(mitgliedIds) {
                    mitglieder = mitgliedRepository.getMitgliederByIds(mitgliedIds)
                }

                MitgliederStoppuhrVerwaltung(mitglieder)
            }
        }
    }
}
