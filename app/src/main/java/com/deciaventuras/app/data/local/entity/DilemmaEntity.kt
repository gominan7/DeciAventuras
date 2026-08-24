package com.deciaventuras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deciaventuras.app.domain.model.Dilemma

/**
 * Fila real en SQLite para un nodo del Mapa de Aventuras.
 * IDs fijos (no autogenerados): los datos semilla y las referencias de
 * [ChoiceEntity.dilemmaId] dependen de conocerlos de antemano.
 */
@Entity(tableName = "dilemmas")
data class DilemmaEntity(
    @PrimaryKey val id: Int,
    val orderIndex: Int,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
)

fun DilemmaEntity.toDomain(): Dilemma = Dilemma(
    id = id,
    orderIndex = orderIndex,
    title = title,
    description = description,
    isUnlocked = isUnlocked,
    isCompleted = isCompleted,
)

fun Dilemma.toEntity(): DilemmaEntity = DilemmaEntity(
    id = id,
    orderIndex = orderIndex,
    title = title,
    description = description,
    isUnlocked = isUnlocked,
    isCompleted = isCompleted,
)
