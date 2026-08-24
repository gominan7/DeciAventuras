package com.deciaventuras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deciaventuras.app.domain.model.UserPreferences

/**
 * Tabla de una sola fila (id siempre = [SINGLETON_ID]). No hay multi-perfil:
 * el dispositivo es de un único explorador.
 */
@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val alias: String,
    val avatarIndex: Int,
    val hapticsEnabled: Boolean,
    val soundEnabled: Boolean = true,
    val onboardingCompleted: Boolean,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}

fun UserPreferencesEntity.toDomain(): UserPreferences = UserPreferences(
    alias = alias,
    avatarIndex = avatarIndex,
    hapticsEnabled = hapticsEnabled,
    soundEnabled = soundEnabled,
    onboardingCompleted = onboardingCompleted,
)

fun UserPreferences.toEntity(): UserPreferencesEntity = UserPreferencesEntity(
    alias = alias,
    avatarIndex = avatarIndex,
    hapticsEnabled = hapticsEnabled,
    soundEnabled = soundEnabled,
    onboardingCompleted = onboardingCompleted,
)
