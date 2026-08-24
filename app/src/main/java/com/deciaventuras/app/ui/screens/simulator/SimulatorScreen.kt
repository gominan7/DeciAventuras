package com.deciaventuras.app.ui.screens.simulator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deciaventuras.app.di.rememberAppContainer
import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.ui.components.CompassDropZone
import com.deciaventuras.app.ui.components.DraggableChoiceCard
import com.deciaventuras.app.ui.components.ExplorerBadge

/**
 * Pantalla B: El Simulador (APP_PROMPT.md §3-B). El niño arrastra una
 * Tarjeta de Decisión hasta la Brújula; al soltarla, se dispara la regla de
 * negocio real (ver [SimulatorViewModel]) y aparecen el Impacto Inmediato y
 * el Destino Final — nunca un simple "Correcto"/"Incorrecto"
 * (Sección 10 del spec maestro).
 */
@Composable
fun SimulatorScreen(
    dilemmaId: Int,
    onFinished: () -> Unit,
) {
    val container = rememberAppContainer()
    val viewModel: SimulatorViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SimulatorViewModel(
                    dilemmaId,
                    container.dilemmaRepository,
                    container.recordChoiceUseCase,
                    container.userPreferencesRepository,
                )
            }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var dropZoneBounds by remember { mutableStateOf<Rect?>(null) }
    var isDropZoneActive by remember { mutableStateOf(false) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading || uiState.dilemma == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                )
                return@Box
            }

            val dilemma = uiState.dilemma!!

            Column(modifier = Modifier.fillMaxSize()) {
                SituationCard(dilemma = dilemma, modifier = Modifier.padding(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CompassDropZone(
                            isActive = isDropZoneActive,
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                dropZoneBounds = coordinates.boundsInWindow()
                            },
                        )
                        if (!uiState.isShowingResult) {
                            Spacer(modifier = Modifier.height(18.dp))
                            DragHint()
                        }
                    }
                }

                if (!uiState.isShowingResult) {
                    // IMPORTANTE: esta fila NO debe tener scroll horizontal.
                    // Un contenedor con scroll intercepta el gesto de arrastre
                    // antes de que la tarjeta lo reciba, y el Drag & Drop deja
                    // de responder por completo. En vez de scroll, se reparten
                    // las tarjetas en filas de a 2 (sin exceder el ancho).
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        uiState.choices.chunked(2).forEach { rowChoices ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowChoices.forEach { choice ->
                                    DraggableChoiceCard(
                                        choice = choice,
                                        dropZoneBounds = { dropZoneBounds },
                                        onDropped = viewModel::onChoiceDropped,
                                        onDragHoverChange = { isDropZoneActive = it },
                                        hapticsEnabled = uiState.hapticsEnabled,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(180.dp))
                }
            }

            AnimatedVisibility(
                visible = uiState.isShowingResult,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                uiState.resultChoice?.let { choice ->
                    ResultPanel(choice = choice, onContinue = onFinished)
                }
            }
        }
    }
}

@Composable
private fun DragHint(modifier: Modifier = Modifier) {
    // Enseña la mecánica de Drag & Drop con una pista visual animada, en vez
    // de depender de que el niño la adivine (Sección 11 del spec maestro:
    // las microanimaciones también sirven para dar pistas de interacción).
    val infiniteTransition = rememberInfiniteTransition(label = "drag_hint")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drag_hint_offset",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .height(26.dp)
                .graphicsLayer { translationY = offsetY },
        )
        Text(
            text = "Mantené presionada tu decisión\ny arrastrala hasta aquí",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun SituationCard(dilemma: Dilemma, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp),
            ) {
                Icon(
                    imageVector = iconForDilemma(dilemma.id),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.height(32.dp),
                )
            }
            Text(
                text = dilemma.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dilemma.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ResultPanel(choice: Choice, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            )
            .padding(20.dp),
    ) {
        EffectRow(
            icon = Icons.Filled.WbSunny,
            label = "Impacto Inmediato",
            text = choice.shortTermEffect,
            accent = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(14.dp))
        EffectRow(
            icon = Icons.Filled.DateRange,
            label = "Destino Final",
            text = choice.longTermEffect,
            accent = MaterialTheme.colorScheme.tertiary,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ganaste una insignia",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            ExplorerBadge(traitName = choice.personalityTrait)
        }

        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Guardar en mi Diario y Continuar")
        }
    }
}

@Composable
private fun EffectRow(icon: ImageVector, label: String, text: String, accent: androidx.compose.ui.graphics.Color) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

private fun iconForDilemma(dilemmaId: Int): ImageVector = when (dilemmaId) {
    1 -> Icons.Filled.AttachMoney
    2 -> Icons.Filled.Groups
    3 -> Icons.Filled.Security
    4 -> Icons.Filled.Bedtime
    5 -> Icons.Filled.Backpack
    else -> Icons.Filled.Security
}
