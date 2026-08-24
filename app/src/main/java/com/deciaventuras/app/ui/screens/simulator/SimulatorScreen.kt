package com.deciaventuras.app.ui.screens.simulator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
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
                SimulatorViewModel(dilemmaId, container.dilemmaRepository, container.recordChoiceUseCase)
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
                    CompassDropZone(
                        isActive = isDropZoneActive,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            dropZoneBounds = coordinates.boundsInWindow()
                        },
                    )
                }

                if (!uiState.isShowingResult) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        uiState.choices.forEach { choice ->
                            DraggableChoiceCard(
                                choice = choice,
                                dropZoneBounds = { dropZoneBounds },
                                onDropped = viewModel::onChoiceDropped,
                                onDragHoverChange = { isDropZoneActive = it },
                            )
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
