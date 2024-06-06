package com.example.myswimsmartdb.ui.Composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TasksTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Aufgabe 1", modifier = Modifier.padding(8.dp))
        Text("Aufgabe 2", modifier = Modifier.padding(8.dp))
        Text("Aufgabe 3", modifier = Modifier.padding(8.dp))
    }
}