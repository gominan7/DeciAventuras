package com.deciaventuras.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.deciaventuras.app.ui.navigation.DeciAventurasNavHost
import com.deciaventuras.app.ui.theme.DeciAventurasTheme

/**
 * Actividad única de la app (patrón recomendado con Navigation Compose).
 * Todo el contenido real vive en [DeciAventurasNavHost], que une el Mapa de
 * Aventuras, el Simulador y el Diario de Explorador.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeciAventurasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    DeciAventurasNavHost()
                }
            }
        }
    }
}
