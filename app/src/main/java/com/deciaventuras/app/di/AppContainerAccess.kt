package com.deciaventuras.app.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.deciaventuras.app.DeciAventurasApp

/**
 * Punto único de acceso al [AppContainer] manual desde Composables, evitando
 * pasarlo como parámetro por cada pantalla. Sustituye a `hiltViewModel()`
 * porque el proyecto no usa Hilt/Dagger (fuera de la Sección 21 del spec
 * maestro: la app es pequeña y 100% offline).
 */
@Composable
fun rememberAppContainer(): AppContainer {
    val context = LocalContext.current.applicationContext as Context
    return (context as DeciAventurasApp).container
}
