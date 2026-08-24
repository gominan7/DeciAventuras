package com.deciaventuras.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Insignia de Explorador (Sección 9 del spec maestro: "colección local").
 * Se dibuja con Canvas a partir del nombre del rasgo — cada insignia se ve
 * distinta según su color derivado, sin depender de un set de imágenes
 * externas ni de URLs de red.
 */
@Composable
fun ExplorerBadge(
    traitName: String,
    modifier: Modifier = Modifier,
) {
    val badgeColor = colorForTrait(traitName)

    Column(
        modifier = modifier.width(84.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(64.dp).padding(4.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension / 2f

            // Forma tipo "escudo/medalla" con puntas (8 picos)
            val points = 8
            val path = androidx.compose.ui.graphics.Path()
            for (i in 0 until points * 2) {
                val angle = Math.PI * i / points
                val r = if (i % 2 == 0) outerRadius else outerRadius * 0.72f
                val x = center.x + (r * cos(angle)).toFloat()
                val y = center.y + (r * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(path = path, color = badgeColor)
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = outerRadius * 0.42f,
                center = center,
            )
            drawCircle(
                color = badgeColor,
                radius = outerRadius * 0.42f,
                center = center,
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        Text(
            text = traitName,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

/**
 * Deriva un color estable a partir del nombre del rasgo, para que cada
 * insignia tenga una identidad visual propia sin necesitar un mapa
 * mantenido a mano por cada trait nuevo que se agregue al contenido.
 */
internal fun colorForTrait(traitName: String): Color {
    val palette = listOf(
        Color(0xFF1E8A4C), // verde jungla
        Color(0xFFF4611E), // naranja fuego
        Color(0xFF2E9FD6), // azul cielo
        Color(0xFF8E5A2E), // marrón tierra
        Color(0xFFB0862E), // dorado brújula
        Color(0xFF5A7DBB), // azul profundo
        Color(0xFF3E9C7A), // verde agua
        Color(0xFFC2542E), // terracota
    )
    val index = kotlin.math.abs(traitName.hashCode()) % palette.size
    return palette[index]
}
