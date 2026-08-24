package com.deciaventuras.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MAX_ALIAS_LENGTH = 18

class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _alias = MutableStateFlow("")
    val alias: StateFlow<String> = _alias.asStateFlow()

    private val _avatarIndex = MutableStateFlow(0)
    val avatarIndex: StateFlow<Int> = _avatarIndex.asStateFlow()

    fun onAliasChanged(value: String) {
        _alias.value = value.take(MAX_ALIAS_LENGTH)
    }

    fun onAvatarSelected(index: Int) {
        _avatarIndex.value = index
    }

    /**
     * Si el alias queda vacío, se guarda "Explorador" por defecto: nunca se
     * bloquea el avance del onboarding por un campo de texto vacío
     * (Sección 29 del spec maestro: casos límite de entrada de usuario).
     */
    fun finishOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            val finalAlias = _alias.value.trim().ifBlank { "Explorador" }
            userPreferencesRepository.updateAlias(finalAlias)
            userPreferencesRepository.updateAvatar(_avatarIndex.value)
            userPreferencesRepository.completeOnboarding()
            onDone()
        }
    }
}
