package com.deciaventuras.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deciaventuras.app.di.rememberAppContainer
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.model.toState
import com.deciaventuras.app.ui.components.DeciAventurasBottomBar
import com.deciaventuras.app.ui.components.DeciAventurasTab
import com.deciaventuras.app.ui.components.DilemmaMapNode
import com.deciaventuras.app.ui.components.ExplorerAvatar
import com.deciaventuras.app.ui.components.MapPathConnector

/**
 * Pantalla A: el Mapa de Aventuras (APP_PROMPT.md §3-A). NO es una lista
 * plana de botones: es un camino serpenteante de nodos ilustrados con
 * estado real (bloqueado/disponible/completado) y un indicador de progreso
 * derivado de los datos, no escrito a mano (Sección 6 del spec maestro).
 */
@Composable
fun DashboardScreen(
    onNavigateToSimulator: (dilemmaId: Int) -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val container = rememberAppContainer()
    val viewModel: DashboardViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DashboardViewModel(container.dilemmaRepository, container.userPreferencesRepository) }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            DeciAventurasBottomBar(
                selectedTab = DeciAventurasTab.MAP,
                onSelectMap = {},
                onSelectJournal = onNavigateToJournal,
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            DashboardHeader(
                alias = uiState.alias,
                avatarIndex = uiState.avatarIndex,
                completedCount = uiState.completedCount,
                totalCount = uiState.totalCount,
                onNavigateToSettings = onNavigateToSettings,
            )
            AdventureMap(
                dilemmas = uiState.dilemmas,
                onNodeClick = { dilemma -> onNavigateToSimulator(dilemma.id) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    alias: String,
    avatarIndex: Int,
    completedCount: Int,
    totalCount: Int,
    onNavigateToSettings: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                ExplorerAvatar(avatarIndex = avatarIndex, size = 44.dp)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = alias.ifBlank { "Explorador" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Mapa de Aventuras",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Ajustes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Cada camino que eliges revela un nuevo destino.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$completedCount de $totalCount aventuras completadas",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AdventureMap(
    dilemmas: List<Dilemma>,
    onNodeClick: (Dilemma) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        itemsIndexed(dilemmas, key = { _, dilemma -> dilemma.id }) { index, dilemma ->
            if (index > 0) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    MapPathConnector()
                }
            }
            val alignToStart = index % 2 == 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (alignToStart) Arrangement.Start else Arrangement.End,
            ) {
                DilemmaMapNode(
                    orderNumber = index + 1,
                    title = dilemma.title,
                    state = dilemma.toState(),
                    onClick = { onNodeClick(dilemma) },
                )
            }
        }
    }
}
