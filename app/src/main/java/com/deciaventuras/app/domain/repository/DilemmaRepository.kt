package com.deciaventuras.app.domain.repository

import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

/**
 * Contrato que expone el dominio hacia los casos de uso. La implementación
 * real (con Room) vive en `data/repository`; esto permite testear la capa
 * de dominio con un fake en memoria, sin tocar Android ni una base de datos
 * real (Sección 22 del spec maestro: "reglas testeables sin UI").
 */
interface DilemmaRepository {

    /** Los dilemas del mapa, ordenados por [Dilemma.orderIndex]. */
    fun observeDilemmas(): Flow<List<Dilemma>>

    suspend fun getDilemma(dilemmaId: Int): Dilemma?

    /** Las tarjetas de decisión disponibles para un dilema concreto. */
    fun observeChoices(dilemmaId: Int): Flow<List<Choice>>

    suspend fun getChoice(choiceId: Int): Choice?

    /** Historial completo de decisiones tomadas (alimenta el Diario). */
    fun observeProgress(): Flow<List<UserProgress>>

    /**
     * Persiste una decisión tomada. NO decide por sí sola qué se desbloquea:
     * esa regla de negocio vive en [com.deciaventuras.app.domain.usecase.RecordChoiceUseCase],
     * para mantener el repositorio como una capa de datos simple y testeable.
     */
    suspend fun insertProgress(progress: UserProgress)

    suspend fun setDilemmaCompleted(dilemmaId: Int, isCompleted: Boolean)

    suspend fun setDilemmaUnlocked(dilemmaId: Int, isUnlocked: Boolean)

    /**
     * Borra todo el historial de decisiones y vuelve los 5 dilemas a su
     * estado inicial (solo el primero desbloqueado, ninguno completado).
     * Usado únicamente desde "Reiniciar todo el progreso" en Ajustes.
     */
    suspend fun resetAllProgress()
}
