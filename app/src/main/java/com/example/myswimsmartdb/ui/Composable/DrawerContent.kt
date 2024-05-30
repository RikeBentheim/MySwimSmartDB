package com.example.myswimsmartdb.ui.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myswimsmartdb.ui.theme.LapisLazuli
import com.example.myswimsmartdb.ui.theme.Platinum

@Composable
fun DrawerContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(LapisLazuli)
    ) {
        Text(
            "Home",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate("home") },
            color = Platinum
        )
        Text(
            "Training",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate("training") },
            color = Platinum
        )
        Text(
            "Neuer Kurs",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate("neuerKurs") },
            color = Platinum
        )
        Text(
            "Stoppuhr",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate("stoppuhr") },
            color = Platinum
        )
        Text(
            "Bahnenschwimmen",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate("bahnenschwimmen") },
            color = Platinum
        )
        Text(
            "Schwimmkurs",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.navigate("Schwimmmkurs") },
            color = Platinum
        )

    }
}
