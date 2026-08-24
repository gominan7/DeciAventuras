package com.deciaventuras.app.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deciaventuras.app.domain.usecase.GetJournalUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Alimenta la Pantalla C (Diario de Explorador): historial + insignias.
 * El estado se deriva por completo de [GetJournalUseCase], que a su vez lee
 * el historial real de Room — nada se calcula "a mano" en la UI.
 */
class JournalViewModel(getJournalUseCase: GetJournalUseCase) : ViewModel() {

    val uiState: StateFlow<GetJournalUseCase.JournalState> = getJournalUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = GetJournalUseCase.JournalState(
                entries = emptyList(),
                earnedBadges = emptyList(),
                completedCount = 0,
                totalCount = 0,
            ),
        )
}
