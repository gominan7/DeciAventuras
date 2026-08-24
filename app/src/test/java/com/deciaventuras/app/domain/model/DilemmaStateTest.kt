package com.deciaventuras.app.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * El estado visual de cada nodo del mapa (Sección 19 del spec maestro:
 * "bloqueado / disponible / completado") se DERIVA de los datos reales,
 * nunca se almacena como un campo aparte que pueda desincronizarse.
 */
class DilemmaStateTest {

    private fun dilemma(isUnlocked: Boolean, isCompleted: Boolean) = Dilemma(
        id = 1,
        orderIndex = 0,
        title = "t",
        description = "d",
        isUnlocked = isUnlocked,
        isCompleted = isCompleted,
    )

    @Test
    fun `no desbloqueado y no completado es LOCKED`() {
        val state = dilemma(isUnlocked = false, isCompleted = false).toState()
        assertThat(state).isEqualTo(DilemmaState.LOCKED)
    }

    @Test
    fun `desbloqueado y no completado es AVAILABLE`() {
        val state = dilemma(isUnlocked = true, isCompleted = false).toState()
        assertThat(state).isEqualTo(DilemmaState.AVAILABLE)
    }

    @Test
    fun `completado es COMPLETED sin importar el flag de desbloqueo`() {
        // Caso límite: un dilema completado siempre debe mostrarse como tal,
        // incluso si por algún motivo isUnlocked quedara en false.
        assertThat(dilemma(isUnlocked = true, isCompleted = true).toState())
            .isEqualTo(DilemmaState.COMPLETED)
        assertThat(dilemma(isUnlocked = false, isCompleted = true).toState())
            .isEqualTo(DilemmaState.COMPLETED)
    }
}
