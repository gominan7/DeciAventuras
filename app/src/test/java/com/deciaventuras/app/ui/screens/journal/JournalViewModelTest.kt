package com.deciaventuras.app.ui.screens.journal

import com.deciaventuras.app.domain.usecase.GetJournalUseCase
import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.deciaventuras.app.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class JournalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `el estado inicial esta vacio cuando no se ha jugado ninguna aventura`() = runTest {
        val repository = FakeDilemmaRepository()
        val viewModel = JournalViewModel(GetJournalUseCase(repository))

        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value

        assertThat(state.entries).isEmpty()
        assertThat(state.earnedBadges).isEmpty()
        assertThat(state.totalCount).isEqualTo(10)
        
        collectJob.cancel()
    }

    @Test
    fun `el estado se actualiza automaticamente al completar una aventura`() = runTest {
        val repository = FakeDilemmaRepository()
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }
        val viewModel = JournalViewModel(GetJournalUseCase(repository))

        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        recordChoice(dilemmaId = 1, choiceId = 2)

        val state = viewModel.uiState.value
        assertThat(state.entries).hasSize(1)
        assertThat(state.earnedBadges).containsExactly("Responsabilidad")
        assertThat(state.completedCount).isEqualTo(1)
        
        collectJob.cancel()
    }
}
