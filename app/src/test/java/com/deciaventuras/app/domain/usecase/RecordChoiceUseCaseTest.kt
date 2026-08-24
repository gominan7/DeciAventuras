package com.deciaventuras.app.domain.usecase

import com.deciaventuras.app.fake.FakeDilemmaRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Prueba la regla de negocio real del Game Loop (Sección 40 del spec
 * maestro), no solo que "cambie un texto" (ejemplo prohibido explícitamente
 * en la Sección 45): guardar decisión → completar → desbloquear siguiente.
 */
class RecordChoiceUseCaseTest {

    private val fixedClock = { 1_700_000_000_000L }

    @Test
    fun `resolver el primer dilema desbloquea el segundo`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        val result = useCase(dilemmaId = 1, choiceId = 2)

        assertThat(result).isEqualTo(RecordChoiceUseCase.Result.UnlockedNext(nextDilemmaId = 2))

        val dilemmas = repository.observeDilemmas().first().associateBy { it.id }
        assertThat(dilemmas.getValue(1).isCompleted).isTrue()
        assertThat(dilemmas.getValue(2).isUnlocked).isTrue()
        assertThat(dilemmas.getValue(2).isCompleted).isFalse()
    }

    @Test
    fun `resolver el primer dilema no desbloquea dilemas mas alla del siguiente`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        useCase(dilemmaId = 1, choiceId = 1)

        val dilemmas = repository.observeDilemmas().first().associateBy { it.id }
        assertThat(dilemmas.getValue(3).isUnlocked).isFalse()
        assertThat(dilemmas.getValue(4).isUnlocked).isFalse()
        assertThat(dilemmas.getValue(5).isUnlocked).isFalse()
    }

    @Test
    fun `resolver la decision guarda el progreso con el timestamp del reloj inyectado`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        useCase(dilemmaId = 1, choiceId = 3)

        val progress = repository.observeProgress().first().single()
        assertThat(progress.dilemmaId).isEqualTo(1)
        assertThat(progress.chosenChoiceId).isEqualTo(3)
        assertThat(progress.timestampMillis).isEqualTo(1_700_000_000_000L)
    }

    @Test
    fun `resolver el dilema 5 ahora desbloquea el 6 (dejo de ser el ultimo tras agregar mas dilemas)`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        val result = useCase(dilemmaId = 5, choiceId = 14)

        assertThat(result).isEqualTo(RecordChoiceUseCase.Result.UnlockedNext(nextDilemmaId = 6))
        assertThat(repository.getDilemma(6)?.isUnlocked).isTrue()
    }

    @Test
    fun `resolver el ultimo dilema (10) no intenta desbloquear nada mas`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        val result = useCase(dilemmaId = 10, choiceId = 29)

        assertThat(result).isEqualTo(RecordChoiceUseCase.Result.NoMoreDilemmas)
        val dilemma10 = repository.getDilemma(10)
        assertThat(dilemma10?.isCompleted).isTrue()
    }

    @Test
    fun `una tarjeta de decision que no pertenece a ese dilema es invalida`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        // choiceId 4 pertenece al dilema 2, no al dilema 1
        val result = useCase(dilemmaId = 1, choiceId = 4)

        assertThat(result).isEqualTo(RecordChoiceUseCase.Result.InvalidChoice)
        assertThat(repository.observeProgress().first()).isEmpty()
        assertThat(repository.getDilemma(1)?.isCompleted).isFalse()
    }

    @Test
    fun `una tarjeta de decision inexistente es invalida`() = runTest {
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        val result = useCase(dilemmaId = 1, choiceId = 9999)

        assertThat(result).isEqualTo(RecordChoiceUseCase.Result.InvalidChoice)
    }

    @Test
    fun `resolver el mismo dilema dos veces no rompe el desbloqueo y conserva ambas decisiones`() = runTest {
        // Caso límite (Sección 29 del spec maestro): doble toque / re-intento.
        val repository = FakeDilemmaRepository()
        val useCase = RecordChoiceUseCase(repository, fixedClock)

        useCase(dilemmaId = 1, choiceId = 1)
        val secondResult = useCase(dilemmaId = 1, choiceId = 2)

        assertThat(secondResult).isEqualTo(RecordChoiceUseCase.Result.UnlockedNext(nextDilemmaId = 2))
        assertThat(repository.observeProgress().first()).hasSize(2)
        assertThat(repository.getDilemma(2)?.isUnlocked).isTrue()
    }
}
