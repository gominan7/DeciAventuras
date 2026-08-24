package com.deciaventuras.app.ui.navigation

/**
 * Las 3 pantallas principales de DeciAventuras (APP_PROMPT.md §3).
 * `Simulator` recibe el id del dilema elegido en el mapa como argumento de ruta.
 */
sealed class Routes(val route: String) {
    data object Dashboard : Routes("dashboard")
    data object Journal : Routes("journal")

    data object Simulator : Routes("simulator/{dilemmaId}") {
        const val ARG_DILEMMA_ID = "dilemmaId"
        fun buildRoute(dilemmaId: Int) = "simulator/$dilemmaId"
    }
}
