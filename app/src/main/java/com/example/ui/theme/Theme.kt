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

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = Color(0xFF00391A),
    primaryContainer = MintDark,
    onPrimaryContainer = Color(0xFFA5F2BC),
    secondary = SageGreen,
    onSecondary = Color(0xFF163720),
    secondaryContainer = Color(0xFF2D4E35),
    onSecondaryContainer = Color(0xFFCEECCF),
    tertiary = Terracotta,
    background = PlantBackgroundDark,
    surface = PlantSurfaceDark,
    surfaceVariant = PlantSurfaceVariantDark,
    onBackground = PlantOnSurfaceDark,
    onSurface = PlantOnSurfaceDark,
    outline = PlantOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = MintLight,
    onPrimaryContainer = Color(0xFF00210E),
    secondary = ForestGreenLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0EFE2),
    onSecondaryContainer = Color(0xFF0A2B14),
    tertiary = Terracotta,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaLight,
    background = PlantBackground,
    surface = PlantSurface,
    surfaceVariant = PlantSurfaceVariant,
    onBackground = PlantOnSurface,
    onSurface = PlantOnSurface,
    outline = PlantOutline
)

@Composable
fun PlantXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve bespoke botanical branding
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
