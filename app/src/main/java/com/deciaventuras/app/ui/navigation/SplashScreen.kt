package com.deciaventuras.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.deciaventuras.app.di.rememberAppContainer
import kotlinx.coroutines.flow.first

/**
 * No dibuja contenido visible más allá del fondo: en cuanto lee el estado
 * real de las preferencias (una sola vez), decide si mandar al explorador
 * al onboarding o directo al Mapa de Aventuras.
 */
@Composable
fun SplashScreen(onOnboardingCompleted: () -> Unit, onOnboardingRequired: () -> Unit) {
    val container = rememberAppContainer()

    LaunchedEffect(Unit) {
        val preferences = container.userPreferencesRepository.observePreferences().first()
        if (preferences.onboardingCompleted) {
            onOnboardingCompleted()
        } else {
            onOnboardingRequired()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {}
}
