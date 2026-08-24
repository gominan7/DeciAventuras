package com.deciaventuras.app.fake

import com.deciaventuras.app.domain.model.UserPreferences
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserPreferencesRepository(
    initial: UserPreferences = UserPreferences(),
) : UserPreferencesRepository {

    private val state = MutableStateFlow(initial)

    override fun observePreferences(): Flow<UserPreferences> = state.asStateFlow()

    override suspend fun updateAlias(alias: String) {
        state.value = state.value.copy(alias = alias)
    }

    override suspend fun updateAvatar(avatarIndex: Int) {
        state.value = state.value.copy(avatarIndex = avatarIndex)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        state.value = state.value.copy(hapticsEnabled = enabled)
    }

    override suspend fun completeOnboarding() {
        state.value = state.value.copy(onboardingCompleted = true)
    }

    override suspend fun clear() {
        state.value = UserPreferences()
    }
}
