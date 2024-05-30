package com.example.layout.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.myswimsmartdb.R

val LapisLazuli = Color(0xFF2f6690)
val Cerulean = Color(0xFF3a7ca5)
val Platinum = Color(0xFFe6e6e6)
val IndigoDye = Color(0xFF16425b)
val SkyBlue = Color(0xFF81c3d7)

// Schriftartendefinition
val Raleway = FontFamily(Font(R.font.raleway))
val Raleway_Medium = FontFamily(Font(R.font.raleway_medium))

// Typografie-Definition
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Raleway_Medium,
        fontSize = 12.sp,
        letterSpacing = 0.25.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Raleway_Medium,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp // Buchstabenabstand leicht erhöhen
    ),
    bodyMedium = TextStyle(
        fontFamily = Raleway,
        fontSize = 12.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Raleway,
        fontSize = 16.sp
    ),
    // Weitere Textstile hinzufügen
)

// Dunkles Farbschema
private val DarkColorScheme = darkColorScheme(
    primary = LapisLazuli,
    secondary = Cerulean
    // Weitere Farben hinzufügen
)

// Helles Farbschema
private val LightColorScheme = lightColorScheme(
    primary = LapisLazuli,
    secondary = Cerulean
    // Weitere Farben hinzufügen
)

@Composable
fun SwimmSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
