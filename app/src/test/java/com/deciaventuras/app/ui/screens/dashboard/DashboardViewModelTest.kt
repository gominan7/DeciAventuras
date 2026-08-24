package com.deciaventuras.app.ui.screens.dashboard

import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.deciaventuras.app.fake.FakeUserPreferencesRepository
import com.deciaventuras.app.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `el estado inicial refleja los 5 dilemas semilla con solo el primero disponible`() = runTest {
        val repository = FakeDilemmaRepository()
        val viewModel = DashboardViewModel(repository, FakeUserPreferencesRepository())

        val state = viewModel.uiState.value

        assertThat(state.dilemmas).hasSize(5)
        assertThat(state.totalCount).isEqualTo(5)
        assertThat(state.completedCount).isEqualTo(0)
        assertThat(state.dilemmas.first().isUnlocked).isTrue()
    }

    @Test
    fun `completar una aventura actualiza el conteo de completadas en el estado`() = runTest {
        val repository = FakeDilemmaRepository()
        val viewModel = DashboardViewModel(repository, FakeUserPreferencesRepository())
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }

        recordChoice(dilemmaId = 1, choiceId = 1)

        assertThat(viewModel.uiState.value.completedCount).isEqualTo(1)
    }

    @Test
    fun `los dilemas se muestran ordenados por orderIndex sin importar el orden de insercion`() = runTest {
        val repository = FakeDilemmaRepository()
        val viewModel = DashboardViewModel(repository, FakeUserPreferencesRepository())

        val orders = viewModel.uiState.value.dilemmas.map { it.orderIndex }

        assertThat(orders).isEqualTo(orders.sorted())
    }

    @Test
    fun `el alias y el avatar guardados en el perfil llegan al estado del mapa`() = runTest {
        val repository = FakeDilemmaRepository()
        val preferences = FakeUserPreferencesRepository(
            initial = com.deciaventuras.app.domain.model.UserPreferences(alias = "Sofía", avatarIndex = 3),
        )
        val viewModel = DashboardViewModel(repository, preferences)

        val state = viewModel.uiState.value

        assertThat(state.alias).isEqualTo("Sofía")
        assertThat(state.avatarIndex).isEqualTo(3)
    }
}

