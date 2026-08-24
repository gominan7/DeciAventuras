package com.deciaventuras.app.domain.usecase

import com.deciaventuras.app.domain.repository.DilemmaRepository
import com.deciaventuras.app.domain.repository.UserPreferencesRepository

/**
 * "Reiniciar todo el progreso" (Ajustes): borra el historial de decisiones,
 * vuelve a bloquear los dilemas, y borra el perfil (alias/avatar), lo que
 * manda de nuevo al onboarding en la próxima apertura. Coordina DOS
 * repositorios — por eso vive como caso de uso y no como un método suelto
 * en cualquiera de los dos (Sección 22 del spec maestro).
 */
class ResetProgressUseCase(
    private val dilemmaRepository: DilemmaRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke() {
        dilemmaRepository.resetAllProgress()
        userPreferencesRepository.clear()
    }
}
