package com.deciaventuras.app.domain.usecase

import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetJournalUseCaseTest {

    @Test
    fun `diario vacio cuando no hay decisiones tomadas`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = GetJournalUseCase(repository)

        val state = useCase.once()

        assertThat(state.entries).isEmpty()
        assertThat(state.earnedBadges).isEmpty()
        assertThat(state.completedCount).isEqualTo(0)
        assertThat(state.totalCount).isEqualTo(10)
    }

    @Test
    fun `cada decision tomada genera una entrada enriquecida con dilema y decision reales`() = runTest {
        val repository = FakeDilemmaRepository()
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }
        val useCase = GetJournalUseCase(repository)

        recordChoice(dilemmaId = 1, choiceId = 2) // trait: Responsabilidad

        val state = useCase.once()

        assertThat(state.entries).hasSize(1)
        val entry = state.entries.single()
        assertThat(entry.dilemma.id).isEqualTo(1)
        assertThat(entry.choice.id).isEqualTo(2)
        assertThat(entry.choice.personalityTrait).isEqualTo("Responsabilidad")
    }

    @Test
    fun `las insignias ganadas no se repiten aunque el rasgo se repita`() = runTest {
        val repository = FakeDilemmaRepository()
        // Ambas decisiones usan el trait "Impulsividad" (dilema 4 y dilema 5)
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }
        val useCase = GetJournalUseCase(repository)

        repository.setDilemmaUnlocked(4, true)
        repository.setDilemmaUnlocked(5, true)
        recordChoice(dilemmaId = 4, choiceId = 10) // Impulsividad
        recordChoice(dilemmaId = 5, choiceId = 13) // Impulsividad

        val state = useCase.once()

        assertThat(state.earnedBadges).containsExactly("Impulsividad")
    }

    @Test
    fun `las entradas del diario quedan ordenadas de la mas reciente a la mas antigua`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = GetJournalUseCase(repository)

        RecordChoiceUseCase(repository) { 1_000L }(dilemmaId = 1, choiceId = 1)
        repository.setDilemmaUnlocked(2, true)
        RecordChoiceUseCase(repository) { 2_000L }(dilemmaId = 2, choiceId = 5)

        val state = useCase.once()

        assertThat(state.entries.map { it.progress.timestampMillis }).isEqualTo(listOf(2_000L, 1_000L))
    }

    @Test
    fun `completedCount refleja solo los dilemas realmente completados`() = runTest {
        val repository = FakeDilemmaRepository()
        val recordChoice = RecordChoiceUseCase(repository) { 1_000L }
        val useCase = GetJournalUseCase(repository)

        recordChoice(dilemmaId = 1, choiceId = 1)

        val state = useCase.once()

        assertThat(state.completedCount).isEqualTo(1)
        assertThat(state.totalCount).isEqualTo(10)
    }
}
