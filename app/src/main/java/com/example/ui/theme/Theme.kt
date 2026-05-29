package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkAcademicPrimary,
    secondary = DarkAcademicSecondary,
    tertiary = DarkAcademicTertiary,
    primaryContainer = DarkAcademicPrimaryContainer,
    onPrimaryContainer = DarkAcademicOnPrimaryContainer,
    background = DarkAcademicBackground,
    surface = DarkAcademicSurface,
    onBackground = DarkAcademicOnSurface,
    onSurface = DarkAcademicOnSurface,
    outline = Color(0xFF404944),
    error = CoralAlert,
    errorContainer = CoralAlertContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AcademicPrimary,
    secondary = AcademicSecondary,
    tertiary = AcademicTertiary,
    primaryContainer = AcademicPrimaryContainer,
    onPrimaryContainer = AcademicOnPrimaryContainer,
    background = AcademicBackground,
    surface = AcademicSurface,
    onBackground = AcademicOnSurface,
    onSurface = AcademicOnSurface,
    outline = Color(0xFFDCE5E0),
    error = CoralAlert,
    errorContainer = CoralAlertContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set default dynamicColor to false to align precisely with requested Bold Typography aesthetic brand colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
