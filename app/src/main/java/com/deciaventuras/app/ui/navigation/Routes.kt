package com.deciaventuras.app.ui.navigation

/**
 * Las pantallas de DeciAventuras. `Splash` decide, sin mostrar nada visible
 * más que el fondo, si el explorador ya completó el onboarding o no, y
 * navega en consecuencia (Sección 16 del spec maestro).
 */
sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Onboarding : Routes("onboarding")
    data object Dashboard : Routes("dashboard")
    data object Journal : Routes("journal")
    data object Settings : Routes("settings")
    data object Celebration : Routes("celebration")

    data object Simulator : Routes("simulator/{dilemmaId}") {
        const val ARG_DILEMMA_ID = "dilemmaId"
        fun buildRoute(dilemmaId: Int) = "simulator/$dilemmaId"
    }
}
