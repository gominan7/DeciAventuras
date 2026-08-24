package com.deciaventuras.app.domain.repository

import com.deciaventuras.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    /** Nunca emite null: si no hay registro guardado todavía, emite [UserPreferences] con sus valores por defecto. */
    fun observePreferences(): Flow<UserPreferences>

    suspend fun updateAlias(alias: String)

    suspend fun updateAvatar(avatarIndex: Int)

    suspend fun setHapticsEnabled(enabled: Boolean)

    suspend fun setSoundEnabled(enabled: Boolean)

    suspend fun completeOnboarding()

    /** Borra el perfil por completo (alias, avatar y preferencias vuelven a sus valores por defecto). */
    suspend fun clear()
}
