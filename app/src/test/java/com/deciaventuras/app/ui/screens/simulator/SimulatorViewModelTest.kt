package com.deciaventuras.app.ui.screens.simulator

import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.deciaventuras.app.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SimulatorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `el estado inicial carga el dilema y sus 3 tarjetas de decision`() = runTest {
        val repository = FakeDilemmaRepository()
        val recordChoice = RecordChoiceUseCase(repository)
        val viewModel = SimulatorViewModel(dilemmaId = 1, repository, recordChoice)

        val state = viewModel.uiState.value

        assertThat(state.isLoading).isFalse()
        assertThat(state.dilemma?.id).isEqualTo(1)
        assertThat(state.choices).hasSize(3)
        assertThat(state.isShowingResult).isFalse()
    }

    @Test
    fun `soltar una tarjeta en la brujula muestra el resultado y persiste la decision`() = runTest {
        val repository = FakeDilemmaRepository()
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }
        val viewModel = SimulatorViewModel(dilemmaId = 1, repository, recordChoice)
        val chosen = viewModel.uiState.value.choices.first { it.id == 2 }

        viewModel.onChoiceDropped(chosen)

        val state = viewModel.uiState.value
        assertThat(state.isShowingResult).isTrue()
        assertThat(state.resultChoice?.id).isEqualTo(2)
        assertThat(repository.observeProgress().first()).hasSize(1)
        assertThat(repository.getDilemma(1)?.isCompleted).isTrue()
        assertThat(repository.getDilemma(2)?.isUnlocked).isTrue()
    }

    @Test
    fun `soltar una segunda tarjeta despues de ya mostrar resultado no hace nada (doble toque)`() = runTest {
        // Caso límite (Sección 29 del spec maestro): doble toque.
        val repository = FakeDilemmaRepository()
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }
        val viewModel = SimulatorViewModel(dilemmaId = 1, repository, recordChoice)
        val choices = viewModel.uiState.value.choices
        val first = choices.first { it.id == 1 }
        val second = choices.first { it.id == 2 }

        viewModel.onChoiceDropped(first)
        viewModel.onChoiceDropped(second)

        assertThat(viewModel.uiState.value.resultChoice?.id).isEqualTo(1)
        assertThat(repository.observeProgress().first()).hasSize(1)
    }
}
