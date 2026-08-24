package com.deciaventuras.app.data.local.entity

import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.model.UserPreferences
import com.deciaventuras.app.domain.model.UserProgress
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * El dominio no debe perder ni alterar datos al pasar por Room y volver.
 * Sin este mapeo probado, un bug de conversión podría corromper el
 * progreso guardado sin que ningún test lo detecte.
 */
class EntityMappingTest {

    @Test
    fun `Dilemma sobrevive el viaje de ida y vuelta a Entity`() {
        val original = Dilemma(
            id = 42,
            orderIndex = 3,
            title = "Título de prueba",
            description = "Descripción de prueba",
            isUnlocked = true,
            isCompleted = false,
        )

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }

    @Test
    fun `Choice sobrevive el viaje de ida y vuelta a Entity`() {
        val original = Choice(
            id = 7,
            dilemmaId = 3,
            orderIndex = 1,
            choiceText = "Texto de la tarjeta",
            shortTermEffect = "Efecto inmediato",
            longTermEffect = "Efecto a largo plazo",
            personalityTrait = "Valentía",
        )

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }

    @Test
    fun `UserProgress sobrevive el viaje de ida y vuelta a Entity`() {
        val original = UserProgress(
            id = 5,
            dilemmaId = 2,
            chosenChoiceId = 6,
            timestampMillis = 1_700_000_000_000,
        )

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }

    @Test
    fun `UserProgress con reflexion tambien sobrevive el viaje de ida y vuelta`() {
        val original = UserProgress(
            id = 8,
            dilemmaId = 1,
            chosenChoiceId = 2,
            timestampMillis = 1_700_000_000_000,
            reflection = "Aprendí que guardar un poco de plata sirve para toda la semana.",
        )

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
        assertThat(roundTripped.reflection).isNotNull()
    }

    @Test
    fun `UserPreferences sobrevive el viaje de ida y vuelta a Entity`() {
        val original = UserPreferences(
            alias = "Explorador07",
            avatarIndex = 5,
            hapticsEnabled = false,
            soundEnabled = false,
            onboardingCompleted = true,
        )

        val roundTripped = original.toEntity().toDomain()

        assertThat(roundTripped).isEqualTo(original)
    }
}
