package com.deciaventuras.app.domain.usecase

import com.deciaventuras.app.domain.model.UserProgress
import com.deciaventuras.app.domain.repository.DilemmaRepository
import kotlinx.coroutines.flow.first

/**
 * Regla de negocio central del Game Loop (Sección 40 del spec maestro):
 *
 *   soltar la tarjeta → guardar la decisión → marcar la aventura como
 *   completada → desbloquear la siguiente aventura del mapa (si existe).
 *
 * Esto es lógica real y testeable, NO "cambiar un texto según qué botón se
 * tocó" (ejemplo explícito prohibido en la Sección 45 del spec maestro).
 */
class RecordChoiceUseCase(
    private val repository: DilemmaRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {

    sealed interface Result {
        /** La decisión se guardó y desbloqueó una nueva aventura. */
        data class UnlockedNext(val nextDilemmaId: Int) : Result

        /** La decisión se guardó pero no había ninguna aventura más que desbloquear. */
        data object NoMoreDilemmas : Result

        /** No existe una tarjeta de decisión con ese id para ese dilema. */
        data object InvalidChoice : Result
    }

    suspend operator fun invoke(dilemmaId: Int, choiceId: Int): Result {
        val choice = repository.getChoice(choiceId)
        if (choice == null || choice.dilemmaId != dilemmaId) {
            return Result.InvalidChoice
        }

        repository.insertProgress(
            UserProgress(
                dilemmaId = dilemmaId,
                chosenChoiceId = choiceId,
                timestampMillis = clock(),
            )
        )
        repository.setDilemmaCompleted(dilemmaId, isCompleted = true)

        val allDilemmas = repository.observeDilemmas().first()
        val current = allDilemmas.firstOrNull { it.id == dilemmaId }
        val nextDilemma = current?.let { thisDilemma ->
            allDilemmas.firstOrNull { it.orderIndex == thisDilemma.orderIndex + 1 }
        }

        return if (nextDilemma != null) {
            if (!nextDilemma.isUnlocked) {
                repository.setDilemmaUnlocked(nextDilemma.id, isUnlocked = true)
            }
            Result.UnlockedNext(nextDilemma.id)
        } else {
            Result.NoMoreDilemmas
        }
    }
}
