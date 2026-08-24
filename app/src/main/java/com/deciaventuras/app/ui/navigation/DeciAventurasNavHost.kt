package com.deciaventuras.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deciaventuras.app.ui.screens.dashboard.DashboardScreen
import com.deciaventuras.app.ui.screens.journal.JournalScreen
import com.deciaventuras.app.ui.screens.simulator.SimulatorScreen

/**
 * Une las 3 pantallas principales de DeciAventuras (APP_PROMPT.md §3) con
 * Navigation Compose. El Mapa es el destino de inicio; el Simulador recibe
 * el id del dilema elegido como argumento de ruta tipado.
 */
@Composable
fun DeciAventurasNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.Dashboard.route) {
        composable(Routes.Dashboard.route) {
            DashboardScreen(
                onNavigateToSimulator = { dilemmaId ->
                    navController.navigate(Routes.Simulator.buildRoute(dilemmaId))
                },
                onNavigateToJournal = {
                    navController.navigate(Routes.Journal.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = Routes.Simulator.route,
            arguments = listOf(navArgument(Routes.Simulator.ARG_DILEMMA_ID) { type = NavType.IntType }),
        ) { backStackEntry ->
            val dilemmaId = backStackEntry.arguments?.getInt(Routes.Simulator.ARG_DILEMMA_ID) ?: return@composable
            SimulatorScreen(
                dilemmaId = dilemmaId,
                onFinished = { navController.popBackStack() },
            )
        }

        composable(Routes.Journal.route) {
            JournalScreen(
                onNavigateToMap = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
