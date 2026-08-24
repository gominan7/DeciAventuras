package com.deciaventuras.app.data.repository

import com.deciaventuras.app.data.local.dao.UserPreferencesDao
import com.deciaventuras.app.data.local.entity.UserPreferencesEntity
import com.deciaventuras.app.data.local.entity.toDomain
import com.deciaventuras.app.domain.model.UserPreferences
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dao: UserPreferencesDao,
) : UserPreferencesRepository {

    override fun observePreferences(): Flow<UserPreferences> =
        dao.observe().map { it?.toDomain() ?: UserPreferences() }

    private suspend fun current(): UserPreferences = observePreferences().first()

    override suspend fun updateAlias(alias: String) {
        save(current().copy(alias = alias))
    }

    override suspend fun updateAvatar(avatarIndex: Int) {
        save(current().copy(avatarIndex = avatarIndex))
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        save(current().copy(hapticsEnabled = enabled))
    }

    override suspend fun completeOnboarding() {
        save(current().copy(onboardingCompleted = true))
    }

    override suspend fun clear() {
        dao.clear()
    }

    private suspend fun save(preferences: UserPreferences) {
        dao.upsert(
            UserPreferencesEntity(
                id = UserPreferencesEntity.SINGLETON_ID,
                alias = preferences.alias,
                avatarIndex = preferences.avatarIndex,
                hapticsEnabled = preferences.hapticsEnabled,
                onboardingCompleted = preferences.onboardingCompleted,
            )
        )
    }
}
