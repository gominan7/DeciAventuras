package com.deciaventuras.app.domain.model

/**
 * Una "Tarjeta de Decisión": una de las 2-3 acciones que el niño puede
 * arrastrar hacia la Brújula en El Simulador.
 *
 * [shortTermEffect] y [longTermEffect] son el corazón pedagógico de la app:
 * cada decisión debe mostrar una consecuencia concreta hoy/ahora y otra la
 * próxima semana, nunca solo "Correcto"/"Incorrecto" (Sección 10 del spec maestro).
 */
data class Choice(
    val id: Int,
    val dilemmaId: Int,
    val orderIndex: Int,
    val choiceText: String,
    val shortTermEffect: String,
    val longTermEffect: String,
    val personalityTrait: String,
)
