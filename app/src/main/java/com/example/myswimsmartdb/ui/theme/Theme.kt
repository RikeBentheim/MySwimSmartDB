package com.example.myswimsmartdb.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.myswimsmartdb.R

val LapisLazuli = Color(0xFF2f6690)
val Cerulean = Color(0xFF3a7ca5)
val Platinum = Color(0xFFe6e6e6)
val IndigoDye = Color(0xFF16425b)
val SkyBlue = Color(0xFF81c3d7)

val Raleway = FontFamily(Font(R.font.raleway))
val Raleway_Medium = FontFamily(Font(R.font.raleway_medium))

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Raleway_Medium,
        fontSize = 12.sp,
        letterSpacing = 0.25.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Raleway_Medium,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Raleway,
        fontSize = 12.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Raleway,
        fontSize = 16.sp
    )
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = LapisLazuli,
            onPrimary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black
        ),
        typography = AppTypography,
        content = content
    )
}
