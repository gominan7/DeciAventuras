package com.deciaventuras.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.repository.DilemmaRepository
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class DashboardUiState(
    val dilemmas: List<Dilemma> = emptyList(),
    val completedCount: Int = 0,
    val isLoading: Boolean = true,
    val alias: String = "",
    val avatarIndex: Int = 0,
) {
    val totalCount: Int get() = dilemmas.size
}

/**
 * Alimenta la Pantalla A (Mapa de Aventuras). El progreso mostrado NO es un
 * número escrito a mano: se calcula siempre a partir de [Dilemma.isCompleted]
 * real, persistido en Room (Sección 45 del spec maestro). El alias/avatar
 * mostrados en el header son el mismo perfil guardado en el Onboarding —
 * se leen de [UserPreferencesRepository], no se vuelven a pedir acá.
 */
class DashboardViewModel(
    private val repository: DilemmaRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        repository.observeDilemmas()
            .onEach { dilemmas ->
                _uiState.value = _uiState.value.copy(
                    dilemmas = dilemmas.sortedBy { it.orderIndex },
                    completedCount = dilemmas.count { it.isCompleted },
                    isLoading = false,
                )
            }
            .launchIn(viewModelScope)

        userPreferencesRepository.observePreferences()
            .onEach { prefs ->
                _uiState.value = _uiState.value.copy(
                    alias = prefs.alias,
                    avatarIndex = prefs.avatarIndex,
                )
            }
            .launchIn(viewModelScope)
    }
}
