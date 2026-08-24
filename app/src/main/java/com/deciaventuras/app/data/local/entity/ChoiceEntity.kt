package com.deciaventuras.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.deciaventuras.app.domain.model.Choice

@Entity(
    tableName = "choices",
    foreignKeys = [
        ForeignKey(
            entity = DilemmaEntity::class,
            parentColumns = ["id"],
            childColumns = ["dilemmaId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("dilemmaId")],
)
data class ChoiceEntity(
    @PrimaryKey val id: Int,
    val dilemmaId: Int,
    val orderIndex: Int,
    val choiceText: String,
    val shortTermEffect: String,
    val longTermEffect: String,
    val personalityTrait: String,
)

fun ChoiceEntity.toDomain(): Choice = Choice(
    id = id,
    dilemmaId = dilemmaId,
    orderIndex = orderIndex,
    choiceText = choiceText,
    shortTermEffect = shortTermEffect,
    longTermEffect = longTermEffect,
    personalityTrait = personalityTrait,
)

fun Choice.toEntity(): ChoiceEntity = ChoiceEntity(
    id = id,
    dilemmaId = dilemmaId,
    orderIndex = orderIndex,
    choiceText = choiceText,
    shortTermEffect = shortTermEffect,
    longTermEffect = longTermEffect,
    personalityTrait = personalityTrait,
)
