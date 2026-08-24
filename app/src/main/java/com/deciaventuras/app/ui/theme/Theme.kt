package com.deciaventuras.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * DeciAventuras usa siempre su identidad propia de "diario de explorador" y
 * NO adopta dynamic color (evita que la app pierda su identidad visual en
 * distintos dispositivos, tal como exige la Sección 42 del spec maestro).
 */
private val LightAdventureScheme = lightColorScheme(
    primary = JungleGreen,
    onPrimary = ParchmentSurface,
    primaryContainer = JungleGreenLight,
    onPrimaryContainer = JungleGreenDark,

    secondary = FireOrange,
    onSecondary = ParchmentSurface,
    secondaryContainer = FireOrangeLight,
    onSecondaryContainer = FireOrangeDark,

    tertiary = SkyBlue,
    onTertiary = ParchmentSurface,
    tertiaryContainer = SkyBlueLight,
    onTertiaryContainer = SkyBlueDark,

    background = ParchmentBackground,
    onBackground = InkBrown,
    surface = ParchmentSurface,
    onSurface = InkBrown,
    surfaceVariant = ParchmentBackground,
    onSurfaceVariant = InkBrownSoft,

    outline = InkBrownSoft,
)

private val DarkAdventureScheme = darkColorScheme(
    primary = JungleGreenLight,
    onPrimary = JungleGreenDark,
    primaryContainer = JungleGreenDark,
    onPrimaryContainer = JungleGreenLight,

    secondary = FireOrangeLight,
    onSecondary = FireOrangeDark,
    secondaryContainer = FireOrangeDark,
    onSecondaryContainer = FireOrangeLight,

    tertiary = SkyBlueLight,
    onTertiary = SkyBlueDark,
    tertiaryContainer = SkyBlueDark,
    onTertiaryContainer = SkyBlueLight,

    background = Color(0xFF1C1712),
    onBackground = Color(0xFFF1E7D8),
    surface = Color(0xFF241D16),
    onSurface = Color(0xFFF1E7D8),
)

@Composable
fun DeciAventurasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkAdventureScheme else LightAdventureScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DeciAventurasTypography,
        content = content,
    )
}
