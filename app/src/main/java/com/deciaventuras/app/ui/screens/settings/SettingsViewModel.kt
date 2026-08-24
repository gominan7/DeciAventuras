package com.deciaventuras.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import com.deciaventuras.app.domain.usecase.ResetProgressUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val resetProgressUseCase: ResetProgressUseCase,
) : ViewModel() {

    val hapticsEnabled: StateFlow<Boolean> = userPreferencesRepository.observePreferences()
        .map { it.hapticsEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = true,
        )

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setHapticsEnabled(enabled) }
    }

    fun resetProgress(onDone: () -> Unit) {
        viewModelScope.launch {
            resetProgressUseCase()
            onDone()
        }
    }
}
