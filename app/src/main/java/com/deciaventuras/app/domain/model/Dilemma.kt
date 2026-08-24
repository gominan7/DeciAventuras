package com.deciaventuras.app.domain.model

/**
 * Representa un "Dilema Cotidiano": un nodo del Mapa de Aventuras.
 *
 * Es un modelo de DOMINIO puro (sin anotaciones de Room ni de Android), para
 * que las reglas de negocio (desbloqueo, progreso, etc.) sean testeables sin
 * necesitar un emulador ni un framework de UI (Sección 22 del spec maestro).
 */
data class Dilemma(
    val id: Int,
    val orderIndex: Int,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
)

/**
 * Estado visual de un nodo del mapa. Se deriva de [Dilemma], nunca se
 * almacena directamente: "el progreso debe derivarse de acciones realizadas"
 * (Sección 45 del spec maestro).
 */
enum class DilemmaState {
    LOCKED,
    AVAILABLE,
    COMPLETED,
}

fun Dilemma.toState(): DilemmaState = when {
    isCompleted -> DilemmaState.COMPLETED
    isUnlocked -> DilemmaState.AVAILABLE
    else -> DilemmaState.LOCKED
}
