package com.deciaventuras.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.deciaventuras.app.domain.model.UserProgress

/**
 * Registro histórico append-only de cada decisión tomada por el niño (nunca
 * se borra desde la app). La única actualización permitida es adjuntar la
 * reflexión propia del niño después de resolver el dilema — el resto de
 * los campos son inmutables una vez insertados.
 */
@Entity(
    tableName = "user_progress",
    foreignKeys = [
        ForeignKey(
            entity = DilemmaEntity::class,
            parentColumns = ["id"],
            childColumns = ["dilemmaId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ChoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["chosenChoiceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("dilemmaId"), Index("chosenChoiceId")],
)
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dilemmaId: Int,
    val chosenChoiceId: Int,
    val timestampMillis: Long,
    val reflection: String? = null,
)

fun UserProgressEntity.toDomain(): UserProgress = UserProgress(
    id = id,
    dilemmaId = dilemmaId,
    chosenChoiceId = chosenChoiceId,
    timestampMillis = timestampMillis,
    reflection = reflection,
)

fun UserProgress.toEntity(): UserProgressEntity = UserProgressEntity(
    id = id,
    dilemmaId = dilemmaId,
    chosenChoiceId = chosenChoiceId,
    timestampMillis = timestampMillis,
    reflection = reflection,
)
