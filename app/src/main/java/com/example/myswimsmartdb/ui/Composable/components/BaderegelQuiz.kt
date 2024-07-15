package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myswimsmartdb.db.entities.Baderegel
import com.example.myswimsmartdb.R
import com.example.myswimsmartdb.ui.theme.Cerulean
import com.example.myswimsmartdb.db.Reposetory.BaderegelRepository

@Composable
fun BaderegelQuiz(baderegelRepository: BaderegelRepository, selectedLevel: Int, onQuizEnd: () -> Unit) {
    var currentRuleIndex by remember { mutableStateOf(0) }
    var showDescription by remember { mutableStateOf(false) }

    // Filter baderegeln based on selectedLevel
    val baderegeln = baderegelRepository.getAllBaderegeln()
    val filteredBaderegeln = baderegeln.filter { it.levels.contains(selectedLevel) }

    if (filteredBaderegeln.isNotEmpty() && currentRuleIndex < filteredBaderegeln.size) {
        val baderegel = filteredBaderegeln[currentRuleIndex]

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Image(
                    painter = painterResource(id = baderegel.imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { showDescription = !showDescription }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (showDescription) {
                    Text(
                        text = baderegel.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (currentRuleIndex + 1 < filteredBaderegeln.size) {
                                currentRuleIndex++
                            } else {
                                onQuizEnd()
                            }
                            showDescription = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(text = if (currentRuleIndex + 1 < filteredBaderegeln.size) "Nächste Baderegel" else "Ende")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ende", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onQuizEnd) {
                Text("Wiederholen")
            }
        }
    }
}


