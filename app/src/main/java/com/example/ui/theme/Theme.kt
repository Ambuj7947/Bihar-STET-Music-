package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ExamLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavySurfaceTint,
    onPrimaryContainer = NavyDark,
    secondary = SprintAmber,
    onSecondary = NavyDark,
    secondaryContainer = SprintAmberSoft,
    onSecondaryContainer = SprintAmberDark,
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldSoft,
    onTertiaryContainer = Color(0xFF14532D),
    error = CrimsonRed,
    onError = Color.White,
    errorContainer = CrimsonSoft,
    onErrorContainer = Color(0xFF991B1B),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SoftInputBg,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun StetMusicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ExamLightColorScheme,
        typography = AppTypography,
        content = content
    )
}
