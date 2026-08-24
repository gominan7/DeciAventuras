package com.deciaventuras.app.domain.usecase

import com.deciaventuras.app.domain.model.UserPreferences
import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.deciaventuras.app.fake.FakeUserPreferencesRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ResetProgressUseCaseTest {

    @Test
    fun `reiniciar progreso borra el historial y vuelve a bloquear los dilemas`() = runTest {
        val dilemmaRepository = FakeDilemmaRepository()
        val preferencesRepository = FakeUserPreferencesRepository()
        val recordChoice = RecordChoiceUseCase(dilemmaRepository) { 1_000L }
        val resetProgress = ResetProgressUseCase(dilemmaRepository, preferencesRepository)

        // Avanza un par de aventuras antes de reiniciar.
        recordChoice(dilemmaId = 1, choiceId = 1)
        recordChoice(dilemmaId = 2, choiceId = 5)

        resetProgress()

        assertThat(dilemmaRepository.observeProgress().first()).isEmpty()
        val dilemmas = dilemmaRepository.observeDilemmas().first().associateBy { it.id }
        assertThat(dilemmas.getValue(1).isUnlocked).isTrue()
        assertThat(dilemmas.getValue(1).isCompleted).isFalse()
        assertThat(dilemmas.getValue(2).isUnlocked).isFalse()
        assertThat(dilemmas.getValue(2).isCompleted).isFalse()
    }

    @Test
    fun `reiniciar progreso tambien borra el perfil (alias, avatar y onboarding)`() = runTest {
        val dilemmaRepository = FakeDilemmaRepository()
        val preferencesRepository = FakeUserPreferencesRepository(
            initial = UserPreferences(
                alias = "Sofía",
                avatarIndex = 3,
                onboardingCompleted = true,
            ),
        )
        val resetProgress = ResetProgressUseCase(dilemmaRepository, preferencesRepository)

        resetProgress()

        val preferences = preferencesRepository.observePreferences().first()
        assertThat(preferences.alias).isEmpty()
        assertThat(preferences.onboardingCompleted).isFalse()
    }
}
