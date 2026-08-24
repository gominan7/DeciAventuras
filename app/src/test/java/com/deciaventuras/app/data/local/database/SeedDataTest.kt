package com.deciaventuras.app.data.local.database

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Valida que los datos semilla cumplan lo exigido por APP_PROMPT.md §5:
 * al menos 5 dilemas reales, cada uno con 2-3 tarjetas de decisión con
 * contenido real (no placeholders ni campos vacíos).
 */
class SeedDataTest {

    @Test
    fun `hay exactamente 5 dilemas semilla`() {
        assertThat(SeedData.dilemmas).hasSize(5)
    }

    @Test
    fun `los ids de dilema son unicos`() {
        val ids = SeedData.dilemmas.map { it.id }
        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun `los orderIndex de dilema son 0,1,2,3,4 sin huecos`() {
        val orders = SeedData.dilemmas.map { it.orderIndex }.sorted()
        assertThat(orders).isEqualTo(listOf(0, 1, 2, 3, 4))
    }

    @Test
    fun `solo el primer dilema (orderIndex 0) empieza desbloqueado`() {
        val unlockedAtStart = SeedData.dilemmas.filter { it.isUnlocked }
        assertThat(unlockedAtStart).hasSize(1)
        assertThat(unlockedAtStart.single().orderIndex).isEqualTo(0)
    }

    @Test
    fun `ningun dilema empieza marcado como completado`() {
        assertThat(SeedData.dilemmas.none { it.isCompleted }).isTrue()
    }

    @Test
    fun `titulo y descripcion de cada dilema no estan vacios`() {
        SeedData.dilemmas.forEach { dilemma ->
            assertThat(dilemma.title.trim()).isNotEmpty()
            assertThat(dilemma.description.trim()).isNotEmpty()
        }
    }

    @Test
    fun `cada dilema tiene entre 2 y 3 tarjetas de decision`() {
        val choicesByDilemma = SeedData.choices.groupBy { it.dilemmaId }
        SeedData.dilemmas.forEach { dilemma ->
            val count = choicesByDilemma[dilemma.id]?.size ?: 0
            assertThat(count).isAtLeast(2)
            assertThat(count).isAtMost(3)
        }
    }

    @Test
    fun `los ids de decision son unicos en toda la app`() {
        val ids = SeedData.choices.map { it.id }
        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun `toda decision apunta a un dilema semilla existente`() {
        val dilemmaIds = SeedData.dilemmas.map { it.id }.toSet()
        SeedData.choices.forEach { choice ->
            assertThat(dilemmaIds).contains(choice.dilemmaId)
        }
    }

    @Test
    fun `ninguna decision tiene campos de texto vacios`() {
        SeedData.choices.forEach { choice ->
            assertThat(choice.choiceText.trim()).isNotEmpty()
            assertThat(choice.shortTermEffect.trim()).isNotEmpty()
            assertThat(choice.longTermEffect.trim()).isNotEmpty()
            assertThat(choice.personalityTrait.trim()).isNotEmpty()
        }
    }

    @Test
    fun `el impacto inmediato y el destino final de cada decision son distintos`() {
        // Sección 10 del spec maestro: no basta con "Correcto/Incorrecto";
        // corto y largo plazo deben aportar información distinta.
        SeedData.choices.forEach { choice ->
            assertThat(choice.shortTermEffect).isNotEqualTo(choice.longTermEffect)
        }
    }

    @Test
    fun `el orderIndex de las decisiones es secuencial dentro de cada dilema`() {
        val choicesByDilemma = SeedData.choices.groupBy { it.dilemmaId }
        choicesByDilemma.forEach { (_, choicesForDilemma) ->
            val orders = choicesForDilemma.map { it.orderIndex }.sorted()
            assertThat(orders).isEqualTo(orders.indices.toList())
        }
    }
}
