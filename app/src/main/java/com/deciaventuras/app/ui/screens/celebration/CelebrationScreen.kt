package com.deciaventuras.app.ui.screens.celebration

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deciaventuras.app.di.rememberAppContainer
import com.deciaventuras.app.ui.components.CompassDropZone
import com.deciaventuras.app.ui.components.ExplorerBadge
import com.deciaventuras.app.ui.screens.journal.JournalViewModel
import com.deciaventuras.app.ui.util.rememberSoundEffects
import kotlinx.coroutines.flow.first

/**
 * Se muestra una única vez por "vuelta completa": cuando la última decisión
 * del último dilema disponible queda registrada (Sección 24 del spec
 * maestro: la app debe sentirse "terminada", no simplemente detenerse sin
 * avisar). Reutiliza [JournalViewModel] porque expone exactamente los datos
 * que hacen falta (insignias ganadas, conteo de aventuras) sin duplicar lógica.
 */
@Composable
fun CelebrationScreen(onBackToMap: () -> Unit) {
    val container = rememberAppContainer()
    val viewModel: JournalViewModel = viewModel(
        factory = viewModelFactory {
            initializer { JournalViewModel(container.getJournalUseCase) }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundEffects = rememberSoundEffects()

    LaunchedEffect(Unit) {
        val soundEnabled = container.userPreferencesRepository.observePreferences().first().soundEnabled
        if (soundEnabled) soundEffects.playCelebration()
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            CompassDropZone(isActive = true)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "¡Completaste todas tus aventuras!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recorriste ${uiState.completedCount} de ${uiState.totalCount} caminos del mapa, " +
                    "y coleccionaste ${uiState.earnedBadges.size} insignias distintas en tu Diario.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tus insignias",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                uiState.earnedBadges.forEach { trait -> ExplorerBadge(traitName = trait) }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Podés seguir volviendo a jugar cualquier aventura para " +
                    "descubrir los caminos que todavía no elegiste.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBackToMap,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Volver al Mapa")
            }
        }
    }
}
