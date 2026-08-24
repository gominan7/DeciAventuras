package com.deciaventuras.app.ui.screens.settings

import com.deciaventuras.app.domain.usecase.ResetProgressUseCase
import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.deciaventuras.app.fake.FakeUserPreferencesRepository
import com.deciaventuras.app.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        preferences: FakeUserPreferencesRepository = FakeUserPreferencesRepository(),
        dilemmas: FakeDilemmaRepository = FakeDilemmaRepository(),
    ) = SettingsViewModel(preferences, ResetProgressUseCase(dilemmas, preferences))

    @Test
    fun `el estado inicial de vibracion y sonido refleja el perfil guardado`() = runTest {
        val preferences = FakeUserPreferencesRepository(
            initial = com.deciaventuras.app.domain.model.UserPreferences(
                hapticsEnabled = false,
                soundEnabled = false,
            ),
        )
        val viewModel = buildViewModel(preferences = preferences)

        assertThat(viewModel.hapticsEnabled.value).isFalse()
        assertThat(viewModel.soundEnabled.value).isFalse()
    }

    @Test
    fun `apagar el sonido lo guarda en las preferencias`() = runTest {
        val preferences = FakeUserPreferencesRepository()
        val viewModel = buildViewModel(preferences = preferences)

        viewModel.setSoundEnabled(false)

        assertThat(preferences.observePreferences().first().soundEnabled).isFalse()
    }

    @Test
    fun `apagar la vibracion no afecta la preferencia de sonido (son independientes)`() = runTest {
        val preferences = FakeUserPreferencesRepository()
        val viewModel = buildViewModel(preferences = preferences)

        viewModel.setHapticsEnabled(false)

        val current = preferences.observePreferences().first()
        assertThat(current.hapticsEnabled).isFalse()
        assertThat(current.soundEnabled).isTrue()
    }

    @Test
    fun `reiniciar progreso llama a onDone`() = runTest {
        val preferences = FakeUserPreferencesRepository()
        val dilemmas = FakeDilemmaRepository()
        val viewModel = buildViewModel(preferences = preferences, dilemmas = dilemmas)
        var onDoneCalled = false

        viewModel.resetProgress { onDoneCalled = true }

        assertThat(onDoneCalled).isTrue()
    }
}
