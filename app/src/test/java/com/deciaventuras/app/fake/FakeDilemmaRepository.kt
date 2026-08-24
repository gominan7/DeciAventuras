package com.deciaventuras.app.fake

import com.deciaventuras.app.data.local.entity.toDomain
import com.deciaventuras.app.data.local.database.SeedData
import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.model.UserProgress
import com.deciaventuras.app.domain.repository.DilemmaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Doble de prueba en memoria: implementa el mismo contrato [DilemmaRepository]
 * que usa la app real, para poder testear los casos de uso de dominio
 * (reglas de negocio) sin depender de Room ni de un dispositivo/emulador
 * Android (Sección 22 del spec maestro).
 */
class FakeDilemmaRepository(
    seedDilemmas: List<Dilemma> = SeedData.dilemmas.map { it.toDomain() },
    seedChoices: List<Choice> = SeedData.choices.map { it.toDomain() },
) : DilemmaRepository {

    private val originalSeedDilemmas: List<Dilemma> = seedDilemmas
    private val dilemmasFlow = MutableStateFlow(seedDilemmas)
    private val choicesById: MutableMap<Int, Choice> = seedChoices.associateBy { it.id }.toMutableMap()
    private val choicesByDilemma: Map<Int, List<Choice>> = seedChoices.groupBy { it.dilemmaId }
    private val progressFlow = MutableStateFlow<List<UserProgress>>(emptyList())
    private var nextProgressId = 1

    override fun observeDilemmas(): Flow<List<Dilemma>> = dilemmasFlow.asStateFlow()

    override suspend fun getDilemma(dilemmaId: Int): Dilemma? =
        dilemmasFlow.value.firstOrNull { it.id == dilemmaId }

    override fun observeChoices(dilemmaId: Int): Flow<List<Choice>> =
        MutableStateFlow(choicesByDilemma[dilemmaId].orEmpty()).asStateFlow()

    override suspend fun getChoice(choiceId: Int): Choice? = choicesById[choiceId]

    override fun observeProgress(): Flow<List<UserProgress>> = progressFlow.asStateFlow()

    override suspend fun insertProgress(progress: UserProgress) {
        val withId = progress.copy(id = nextProgressId++)
        progressFlow.value = progressFlow.value + withId
    }

    override suspend fun setDilemmaCompleted(dilemmaId: Int, isCompleted: Boolean) {
        dilemmasFlow.value = dilemmasFlow.value.map {
            if (it.id == dilemmaId) it.copy(isCompleted = isCompleted) else it
        }
    }

    override suspend fun setDilemmaUnlocked(dilemmaId: Int, isUnlocked: Boolean) {
        dilemmasFlow.value = dilemmasFlow.value.map {
            if (it.id == dilemmaId) it.copy(isUnlocked = isUnlocked) else it
        }
    }

    override suspend fun resetAllProgress() {
        progressFlow.value = emptyList()
        dilemmasFlow.value = originalSeedDilemmas
    }

    override suspend fun updateReflection(progressId: Int, reflection: String) {
        progressFlow.value = progressFlow.value.map {
            if (it.id == progressId) it.copy(reflection = reflection) else it
        }
    }
}
