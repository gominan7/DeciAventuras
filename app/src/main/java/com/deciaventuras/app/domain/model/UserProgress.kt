package com.deciaventuras.app.domain.model

/**
 * Un registro histórico: "el [chosenChoiceId] día que el niño resolvió
 * [dilemmaId]". Es append-only (nunca se sobrescribe ni se borra), porque
 * alimenta el Diario de Explorador y las insignias ganadas — el historial
 * debe persistirse de verdad, no derivarse de un solo campo mutable
 * (Sección 45 del spec maestro, regla "historial debe persistirse").
 */
data class UserProgress(
    val id: Int = 0,
    val dilemmaId: Int,
    val chosenChoiceId: Int,
    val timestampMillis: Long,
    /** La reflexión propia del niño, escrita opcionalmente al terminar el dilema. */
    val reflection: String? = null,
)

/**
 * Una entrada ya "enriquecida" del Diario de Explorador: combina el registro
 * histórico con el texto real del dilema y de la decisión tomada, lista para
 * mostrarse en la pantalla C sin que la UI tenga que hacer joins manuales.
 */
data class JournalEntry(
    val progress: UserProgress,
    val dilemma: Dilemma,
    val choice: Choice,
)
