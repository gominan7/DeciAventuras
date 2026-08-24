package com.deciaventuras.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.deciaventuras.app.domain.model.Choice
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * La "Tarjeta de Decisión" que el niño arrastra hacia la Brújula.
 * Mecánica principal de la app (APP_PROMPT.md §1): Drag & Drop real con
 * `detectDragGestures`, NO un botón que dispara un texto al tocarlo.
 *
 * @param dropZoneBounds límites de la Brújula, en coordenadas de ventana,
 *   reportados por el `Box` contenedor compartido en [SimulatorScreen].
 * @param onDropped se invoca solo cuando la tarjeta se suelta encima de la Brújula.
 * @param onDragHoverChange notifica al padre si esta tarjeta está siendo
 *   arrastrada sobre la Brújula ahora mismo, para que la Brújula "brille".
 */
@Composable
fun DraggableChoiceCard(
    choice: Choice,
    dropZoneBounds: () -> Rect?,
    onDropped: (Choice) -> Unit,
    modifier: Modifier = Modifier,
    onDragHoverChange: (Boolean) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val offset = remember(choice.id) { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isDragging by remember { mutableStateOf(false) }
    var cardCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val haptics = LocalHapticFeedback.current

    fun isOverlappingDropZone(): Boolean {
        val card = cardCoordinates ?: return false
        val zone = dropZoneBounds() ?: return false
        return card.boundsInWindow().overlaps(zone)
    }

    Box(
        modifier = modifier
            .zIndex(if (isDragging) 1f else 0f)
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .graphicsLayer {
                rotationZ = if (isDragging) -4f else 0f
                scaleX = if (isDragging) 1.06f else 1f
                scaleY = if (isDragging) 1.06f else 1f
            }
            .onGloballyPositioned { cardCoordinates = it }
            .pointerInput(choice.id) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragCancel = {
                        isDragging = false
                        onDragHoverChange(false)
                        scope.launch { offset.animateTo(Offset.Zero, spring()) }
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragHoverChange(false)
                        if (isOverlappingDropZone()) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDropped(choice)
                        } else {
                            scope.launch { offset.animateTo(Offset.Zero, spring()) }
                        }
                    },
                ) { change, dragAmount ->
                    change.consume()
                    scope.launch { offset.snapTo(offset.value + dragAmount) }
                    onDragHoverChange(isOverlappingDropZone())
                }
            },
    ) {
        Card(
            modifier = Modifier.widthIn(min = 130.dp, max = 160.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 10.dp else 3.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(14.dp),
            ) {
                Text(
                    text = choice.choiceText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

