package com.example.myswimsmartdb.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.layout.ui.theme.IndigoDye
import com.example.layout.ui.theme.Platinum
import com.example.layout.ui.theme.Cerulean
import com.example.layout.ui.theme.SkyBlue
import com.example.layout.ui.theme.LapisLazuli



object AppTheme {
    val buttonHeight = 56.dp
    val buttonHeightsmall = 25.dp
    val buttonWidth = 200.dp
    val buttonWidthsmal = 85.dp
    val cornerShape = RoundedCornerShape(0.dp) // Eckig
    val containerColor = SkyBlue
    val borderColor = IndigoDye
    val textColorHell = Platinum
    val textColorDunkel = IndigoDye
    val padding = 1.dp
}

// Optionale Erweiterung für MaterialTheme
val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp) // Eckige Ecken für große Komponenten
)

