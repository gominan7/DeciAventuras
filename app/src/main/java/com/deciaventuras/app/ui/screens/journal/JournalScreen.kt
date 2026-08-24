package com.deciaventuras.app.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deciaventuras.app.di.rememberAppContainer
import com.deciaventuras.app.domain.model.JournalEntry
import com.deciaventuras.app.domain.usecase.GetJournalUseCase
import com.deciaventuras.app.ui.components.DeciAventurasBottomBar
import com.deciaventuras.app.ui.components.DeciAventurasTab
import com.deciaventuras.app.ui.components.ExplorerBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla C: el Diario de Explorador (APP_PROMPT.md §3-C). Un inventario
 * visual real: las insignias se derivan de decisiones tomadas de verdad
 * (nunca se "desbloquean" a mano), y el estado vacío invita a jugar en vez
 * de mostrar una pantalla en blanco (Sección 41 del spec maestro).
 */
@Composable
fun JournalScreen(onNavigateToMap: () -> Unit) {
    val container = rememberAppContainer()
    val viewModel: JournalViewModel = viewModel(
        factory = viewModelFactory {
            initializer { JournalViewModel(container.getJournalUseCase) }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            DeciAventurasBottomBar(
                selectedTab = DeciAventurasTab.JOURNAL,
                onSelectMap = onNavigateToMap,
                onSelectJournal = {},
            )
        },
    ) { padding ->
        if (uiState.entries.isEmpty()) {
            EmptyJournalState(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            JournalContent(
                badges = uiState.earnedBadges,
                entries = uiState.entries,
                completedCount = uiState.completedCount,
                totalCount = uiState.totalCount,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
        }
    }
}

@Composable
private fun JournalContent(
    badges: List<String>,
    entries: List<JournalEntry>,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text(
                    text = "Diario de Explorador",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "$completedCount de $totalCount aventuras vividas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Text(
                text = "Mis insignias",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                badges.forEach { trait -> ExplorerBadge(traitName = trait) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Mis decisiones",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }

        items(entries, key = { it.progress.id }) { entry ->
            JournalEntryCard(entry = entry, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = entry.dilemma.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = formatTimestamp(entry.progress.timestampMillis),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Elegiste: ${entry.choice.choiceText}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "→ ${entry.choice.longTermEffect}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val reflection = entry.progress.reflection
            if (!reflection.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Tu reflexión",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "\u201C$reflection\u201D",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyJournalState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50),
                    )
                    .padding(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoStories,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(48.dp),
                )
            }
            Text(
                text = "Tu diario todavía está en blanco",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ve al mapa y vive tu primera aventura para empezar a coleccionar insignias.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

private fun formatTimestamp(millis: Long): String =
    SimpleDateFormat("d MMM", Locale("es", "ES")).format(Date(millis))
