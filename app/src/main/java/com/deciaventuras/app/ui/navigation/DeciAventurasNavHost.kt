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
import com.deciaventuras.app.ui.screens.onboarding.OnboardingScreen
import com.deciaventuras.app.ui.screens.settings.SettingsScreen
import com.deciaventuras.app.ui.screens.simulator.SimulatorScreen

/**
 * Une todas las pantallas de DeciAventuras con Navigation Compose.
 * `Splash` es el punto de entrada real: decide sin mostrar nada visible más
 * que el fondo si hay que mostrar el Onboarding (primera vez) o ir directo
 * al Mapa de Aventuras (Sección 16 del spec maestro).
 */
@Composable
fun DeciAventurasNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(
                onOnboardingCompleted = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onOnboardingRequired = {
                    navController.navigate(Routes.Onboarding.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

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
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings.route)
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

        composable(Routes.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onProgressReset = {
                    // El progreso y el perfil se borraron: hay que volver a
                    // pasar por el Onboarding, no solo por el Mapa.
                    navController.navigate(Routes.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
