package com.deciaventuras.app.domain.usecase

import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.model.JournalEntry
import com.deciaventuras.app.domain.repository.DilemmaRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Construye el contenido real de la Pantalla C (Diario de Explorador):
 * une el historial de [UserProgress] con el texto del dilema y de la
 * decisión elegida, y calcula qué insignias (rasgos únicos) se ganaron.
 *
 * "colección: incorrecto = mostrar imágenes bloqueadas; correcto =
 * desbloquear elementos según acciones reales" (Sección 45 del spec maestro).
 * Aquí las insignias se derivan siempre de [UserProgress] real, nunca de un
 * contador manual.
 */
class GetJournalUseCase(private val repository: DilemmaRepository) {

    data class JournalState(
        val entries: List<JournalEntry>,
        val earnedBadges: List<String>,
        val completedCount: Int,
        val totalCount: Int,
    )

    operator fun invoke() = flow {
        combine(
            repository.observeProgress(),
            repository.observeDilemmas(),
        ) { progressList, dilemmas ->
            val dilemmasById = dilemmas.associateBy(Dilemma::id)

            val entries = progressList.mapNotNull { progress ->
                val dilemma = dilemmasById[progress.dilemmaId] ?: return@mapNotNull null
                val choice = repository.getChoice(progress.chosenChoiceId) ?: return@mapNotNull null
                JournalEntry(progress = progress, dilemma = dilemma, choice = choice)
            }.sortedByDescending { it.progress.timestampMillis }

            val earnedBadges = entries
                .map { it.choice.personalityTrait }
                .distinct()
                .sorted()

            JournalState(
                entries = entries,
                earnedBadges = earnedBadges,
                completedCount = dilemmas.count { it.isCompleted },
                totalCount = dilemmas.size,
            )
        }.collect { emit(it) }
    }

    /** Variante suspend de un único cálculo, útil en tests y previews. */
    suspend fun once(): JournalState = invoke().first()
}
