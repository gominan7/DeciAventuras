package com.deciaventuras.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deciaventuras.app.domain.model.DilemmaState

/**
 * Un nodo del Mapa de Aventuras: círculo ilustrado con Canvas (no un ícono
 * Material solo) que muestra su estado con iconografía real, no solo color
 * (Sección 19 del spec maestro: "no expresarlo únicamente mediante color").
 */
@Composable
fun DilemmaMapNode(
    orderNumber: Int,
    title: String,
    state: DilemmaState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isClickable = state != DilemmaState.LOCKED

    val ringColor = when (state) {
        DilemmaState.LOCKED -> MaterialTheme.colorScheme.outline
        DilemmaState.AVAILABLE -> MaterialTheme.colorScheme.secondary
        DilemmaState.COMPLETED -> MaterialTheme.colorScheme.primary
    }
    val fillColor = when (state) {
        DilemmaState.LOCKED -> MaterialTheme.colorScheme.surfaceVariant
        DilemmaState.AVAILABLE -> MaterialTheme.colorScheme.secondaryContainer
        DilemmaState.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
    }

    Column(
        modifier = modifier
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(8.dp)
            .width(96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(72.dp)) {
                val strokeWidth = 4.dp.toPx()
                drawCircle(color = fillColor, radius = size.minDimension / 2f - strokeWidth)
                drawCircle(
                    color = ringColor,
                    radius = size.minDimension / 2f - strokeWidth / 2f,
                    style = Stroke(width = strokeWidth),
                )
                // Rayitas tipo "sol/brújula" decorativas alrededor del nodo disponible/completado
                if (state != DilemmaState.LOCKED) {
                    val tickCount = 8
                    repeat(tickCount) { i ->
                        val angle = (2 * Math.PI / tickCount) * i
                        val outer = size.minDimension / 2f + 6.dp.toPx()
                        val inner = size.minDimension / 2f + 1.dp.toPx()
                        val dx = kotlin.math.cos(angle).toFloat()
                        val dy = kotlin.math.sin(angle).toFloat()
                        val center = Offset(size.width / 2f, size.height / 2f)
                        drawLine(
                            color = ringColor.copy(alpha = 0.5f),
                            start = Offset(center.x + dx * inner, center.y + dy * inner),
                            end = Offset(center.x + dx * outer, center.y + dy * outer),
                            strokeWidth = 2.dp.toPx(),
                        )
                    }
                }
            }

            when (state) {
                DilemmaState.LOCKED -> Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Aventura bloqueada",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(26.dp),
                )
                DilemmaState.COMPLETED -> Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Aventura completada",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
                DilemmaState.AVAILABLE -> Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$orderNumber",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = if (state == DilemmaState.LOCKED) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/** Línea punteada que conecta dos nodos del mapa (Sección "Dashboard" de APP_PROMPT.md). */
@Composable
fun MapPathConnector(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.outline
    Canvas(modifier = modifier.size(width = 4.dp, height = 32.dp)) {
        val dashLength = 6.dp.toPx()
        val gapLength = 5.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = color,
                start = Offset(size.width / 2f, y),
                end = Offset(size.width / 2f, (y + dashLength).coerceAtMost(size.height)),
                strokeWidth = 3.dp.toPx(),
            )
            y += dashLength + gapLength
        }
    }
}
