package com.example.myswimsmartdb.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.Composable.components.StoppuhrContent
import com.example.myswimsmartdb.ui.theme.Platinum

@Composable
fun StoppuhrScreen(navController: NavHostController) {
    BasisScreen(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header-Bild
            Image(
                painter = painterResource(id = R.drawable.adobestock_288862937),
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
            StoppuhrContent()
        }
    }
}