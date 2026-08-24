package com.deciaventuras.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deciaventuras.app.ui.theme.FireOrange
import com.deciaventuras.app.ui.theme.FireOrangeDark
import com.deciaventuras.app.ui.theme.JungleGreen
import com.deciaventuras.app.ui.theme.JungleGreenDark
import com.deciaventuras.app.ui.theme.JungleGreenLight
import com.deciaventuras.app.ui.theme.SkyBlue
import com.deciaventuras.app.ui.theme.SkyBlueDark
import com.deciaventuras.app.ui.theme.SkyBlueLight

/**
 * Los 8 colores de pelaje disponibles para el avatar, en el mismo lenguaje
 * de color que el resto de la app (nada de imágenes externas: Sección 23
 * del spec maestro, 100% offline).
 */
val AVATAR_COLORS: List<Color> = listOf(
    JungleGreen, FireOrange, SkyBlue, JungleGreenDark,
    FireOrangeDark, SkyBlueDark, JungleGreenLight, SkyBlueLight,
)

/**
 * El "zorrito explorador" — el personaje guía mencionado en el diseño
 * original de la app (APP_PROMPT.md §1) — como avatar ilustrado con Canvas,
 * no una imagen importada. Cada [avatarIndex] usa un color de pelaje distinto
 * de [AVATAR_COLORS].
 */
@Composable
fun ExplorerAvatar(
    avatarIndex: Int,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val furColor = AVATAR_COLORS[avatarIndex % AVATAR_COLORS.size]
    val snoutColor = Color(0xFFFFF6E9)
    val featureColor = Color(0xFF3E2C23)

    var boxModifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface)
    if (isSelected) {
        boxModifier = boxModifier.border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
    }
    if (onClick != null) {
        boxModifier = boxModifier.clickable(onClick = onClick)
    }

    Canvas(modifier = boxModifier) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val r = this.size.minDimension / 2f

        // Orejas triangulares (características de zorro)
        val leftEar = Path().apply {
            moveTo(center.x - r * 0.55f, center.y - r * 0.45f)
            lineTo(center.x - r * 0.85f, center.y - r * 1.05f)
            lineTo(center.x - r * 0.10f, center.y - r * 0.70f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(center.x + r * 0.55f, center.y - r * 0.45f)
            lineTo(center.x + r * 0.85f, center.y - r * 1.05f)
            lineTo(center.x + r * 0.10f, center.y - r * 0.70f)
            close()
        }
        drawPath(path = leftEar, color = furColor)
        drawPath(path = rightEar, color = furColor)

        // Cabeza
        drawCircle(color = furColor, radius = r * 0.82f, center = center)

        // Hocico color crema
        drawOval(
            color = snoutColor,
            topLeft = Offset(center.x - r * 0.34f, center.y + r * 0.04f),
            size = Size(r * 0.68f, r * 0.52f),
        )

        // Nariz
        drawCircle(color = featureColor, radius = r * 0.09f, center = Offset(center.x, center.y + r * 0.20f))

        // Ojos
        drawCircle(color = featureColor, radius = r * 0.09f, center = Offset(center.x - r * 0.30f, center.y - r * 0.05f))
        drawCircle(color = featureColor, radius = r * 0.09f, center = Offset(center.x + r * 0.30f, center.y - r * 0.05f))
    }
}
