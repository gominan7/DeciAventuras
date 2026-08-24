package com.deciaventuras.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * La Brújula de Acción / Portal: zona central donde se sueltan las Tarjetas
 * de Decisión (APP_PROMPT.md §3-B). Ilustrada con Compose Canvas (prioridad
 * 3 de la Sección 4 del spec maestro), no con un ícono Material genérico.
 *
 * @param isActive true mientras una tarjeta se arrastra por encima: la
 *   Brújula "respira" (escala + brillo) como feedback visual inmediato
 *   (Sección 11 del spec maestro: microanimaciones de selección).
 */
@Composable
fun CompassDropZone(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.12f else 1f,
        animationSpec = tween(180),
        label = "compass_scale",
    )

    val jungleGreen = MaterialTheme.colorScheme.primary
    val fireOrange = MaterialTheme.colorScheme.secondary
    val skyBlue = MaterialTheme.colorScheme.tertiary
    val parchment = MaterialTheme.colorScheme.surface
    val ink = MaterialTheme.colorScheme.onSurface
    val glow = MaterialTheme.colorScheme.secondary.copy(alpha = if (isActive) 0.35f else 0f)

    Canvas(
        modifier = modifier
            .size(140.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Halo de energía cuando está activa (Sección 11: "destello")
        if (isActive) {
            drawCircle(color = glow, radius = radius * 1.25f, center = center)
        }

        // Anillo exterior verde jungla
        drawCircle(color = jungleGreen, radius = radius, center = center)

        // Disco de pergamino interior
        val ringRadius = radius * 0.72f
        drawCircle(color = parchment, radius = ringRadius, center = center)
        drawCircle(
            color = jungleGreen,
            radius = ringRadius,
            center = center,
            style = Stroke(width = 3.dp.toPx()),
        )

        // Marcas cardinales
        val tickOuter = ringRadius * 0.95f
        val tickInner = ringRadius * 0.80f
        for (angleDeg in 0 until 360 step 45) {
            val angle = Math.toRadians(angleDeg.toDouble())
            val dx = sin(angle).toFloat()
            val dy = -cos(angle).toFloat()
            drawLine(
                color = ink.copy(alpha = 0.55f),
                start = Offset(center.x + dx * tickInner, center.y + dy * tickInner),
                end = Offset(center.x + dx * tickOuter, center.y + dy * tickOuter),
                strokeWidth = 2.dp.toPx(),
            )
        }

        // Aguja: mitad naranja fuego (norte) / mitad azul cielo (sur)
        val needleLen = ringRadius * 0.60f
        val needleHalfWidth = ringRadius * 0.15f

        val northTip = Offset(center.x, center.y - needleLen)
        val southTip = Offset(center.x, center.y + needleLen)
        val leftBase = Offset(center.x - needleHalfWidth, center.y)
        val rightBase = Offset(center.x + needleHalfWidth, center.y)

        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(northTip.x, northTip.y)
                lineTo(leftBase.x, leftBase.y)
                lineTo(rightBase.x, rightBase.y)
                close()
            },
            color = fireOrange,
        )
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(southTip.x, southTip.y)
                lineTo(leftBase.x, leftBase.y)
                lineTo(rightBase.x, rightBase.y)
                close()
            },
            color = skyBlue,
        )

        drawCircle(color = ink, radius = ringRadius * 0.08f, center = center)
    }
}

/** Referencia de color usada solo como fallback si se necesita fuera de un contexto Composable con MaterialTheme. */
internal val CompassFallbackColor = Color(0xFF1E8A4C)
