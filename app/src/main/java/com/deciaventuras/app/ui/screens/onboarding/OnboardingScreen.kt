package com.deciaventuras.app.ui.screens.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deciaventuras.app.di.rememberAppContainer
import com.deciaventuras.app.ui.components.AVATAR_COLORS
import com.deciaventuras.app.ui.components.CompassDropZone
import com.deciaventuras.app.ui.components.ExplorerAvatar

/**
 * Onboarding de 3 pantallas (dentro del máximo de la Sección 16 del spec
 * maestro): bienvenida, cómo se juega (refuerza la mecánica de Drag & Drop
 * desde el primer momento), y perfil (alias + avatar). Aparece una única
 * vez; después de "Comenzar" no vuelve a mostrarse.
 */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val container = rememberAppContainer()
    val viewModel: OnboardingViewModel = viewModel(
        factory = viewModelFactory {
            initializer { OnboardingViewModel(container.userPreferencesRepository) }
        },
    )
    var page by remember { mutableStateOf(0) }
    val alias by viewModel.alias.collectAsState()
    val avatarIndex by viewModel.avatarIndex.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Crossfade(targetState = page, label = "onboarding_page") { currentPage ->
                    when (currentPage) {
                        0 -> WelcomePage()
                        1 -> HowToPlayPage()
                        else -> ProfilePage(
                            alias = alias,
                            avatarIndex = avatarIndex,
                            onAliasChanged = viewModel::onAliasChanged,
                            onAvatarSelected = viewModel::onAvatarSelected,
                        )
                    }
                }
            }

            PageDots(total = 3, current = page)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (page > 0) {
                    TextButton(onClick = { page -= 1 }) { Text("Atrás") }
                } else {
                    Spacer(modifier = Modifier.size(1.dp))
                }

                Button(
                    onClick = {
                        if (page < 2) {
                            page += 1
                        } else {
                            viewModel.finishOnboarding(onFinished)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(if (page < 2) "Siguiente" else "Comenzar")
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CompassDropZone(isActive = false)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¡Bienvenido, explorador!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "DeciAventuras es un gran mapa de exploración. Cada decisión que tomes " +
                "abre un camino distinto y revela un nuevo destino.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HowToPlayPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Explore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Así se juega",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        HowToStep(number = "1", text = "Leé la situación de cada aventura.")
        HowToStep(number = "2", text = "Mantené presionada tu decisión y arrastrala hasta la brújula.")
        HowToStep(number = "3", text = "Descubrí el Impacto Inmediato y el Destino Final de tu elección.")
    }
}

@Composable
private fun HowToStep(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = number, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ProfilePage(
    alias: String,
    avatarIndex: Int,
    onAliasChanged: (String) -> Unit,
    onAvatarSelected: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Elegí tu perfil de explorador",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "No hace falta tu nombre real ni ningún dato personal.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = alias,
            onValueChange = onAliasChanged,
            label = { Text("Tu alias de explorador") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Elegí tu avatar",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(120.dp),
        ) {
            items(AVATAR_COLORS.size) { index ->
                ExplorerAvatar(
                    avatarIndex = index,
                    size = 52.dp,
                    isSelected = index == avatarIndex,
                    onClick = { onAvatarSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun PageDots(total: Int, current: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == current) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == current) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                    ),
            )
        }
    }
}
