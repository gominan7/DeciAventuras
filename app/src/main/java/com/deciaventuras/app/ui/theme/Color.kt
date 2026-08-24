package com.deciaventuras.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta "Diario de Explorador" de DeciAventuras.
 * Identidad visual propia (Sección 3 y 42 del spec maestro): no se reutiliza
 * la paleta de otras apps de la misma familia educativa.
 */

// Verde jungla — color primario, exploración y naturaleza
val JungleGreen = Color(0xFF1E8A4C)
val JungleGreenDark = Color(0xFF0F5C31)
val JungleGreenLight = Color(0xFFB8E6C9)

// Naranja fuego — color secundario, energía y acción (Brújula / decisiones)
val FireOrange = Color(0xFFF4611E)
val FireOrangeDark = Color(0xFFC24713)
val FireOrangeLight = Color(0xFFFFD3BA)

// Azul cielo — color terciario, horizonte y descubrimiento
val SkyBlue = Color(0xFF2E9FD6)
val SkyBlueDark = Color(0xFF1B6E9C)
val SkyBlueLight = Color(0xFFC9ECFB)

// Tonos "papel de mapa" — fondos y superficies con textura de diario de explorador
val ParchmentBackground = Color(0xFFF7EEDD)
val ParchmentSurface = Color(0xFFFFFBF2)
val InkBrown = Color(0xFF3E2C23)
val InkBrownSoft = Color(0xFF6B5344)

// Estados de progreso (usados en mapa, insignias y dilemas)
val StateLocked = Color(0xFF9E9384)
val StateAvailable = SkyBlue
val StateCompleted = JungleGreen
val StateInProgress = FireOrange
