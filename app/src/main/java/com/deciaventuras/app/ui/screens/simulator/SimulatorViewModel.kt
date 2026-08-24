package com.deciaventuras.app.ui.screens.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.repository.DilemmaRepository
import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SimulatorUiState(
    val dilemma: Dilemma? = null,
    val choices: List<Choice> = emptyList(),
    /** No nulo únicamente cuando ya se soltó una tarjeta: dispara la vista de consecuencias. */
    val resultChoice: Choice? = null,
    val isLoading: Boolean = true,
) {
    val isShowingResult: Boolean get() = resultChoice != null
}

/**
 * Alimenta la Pantalla B (El Simulador). No es "cambiar un texto según qué
 * botón se tocó": [onChoiceDropped] dispara [RecordChoiceUseCase], la regla
 * de negocio real que persiste la decisión y calcula el desbloqueo
 * (Sección 45 del spec maestro).
 */
class SimulatorViewModel(
    private val dilemmaId: Int,
    private val repository: DilemmaRepository,
    private val recordChoiceUseCase: RecordChoiceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val dilemma = repository.getDilemma(dilemmaId)
            val choices = repository.observeChoices(dilemmaId)
            choices.collect { choiceList ->
                _uiState.value = _uiState.value.copy(
                    dilemma = dilemma,
                    choices = choiceList.sortedBy { it.orderIndex },
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Se llama cuando el niño suelta una Tarjeta de Decisión dentro de la
     * Brújula. Ignora sueltas repetidas si ya se mostró un resultado (evita
     * doble registro por doble toque, Sección 29 del spec maestro).
     */
    fun onChoiceDropped(choice: Choice) {
        if (_uiState.value.isShowingResult) return
        _uiState.value = _uiState.value.copy(resultChoice = choice)
        viewModelScope.launch {
            recordChoiceUseCase(dilemmaId = dilemmaId, choiceId = choice.id)
        }
    }
}
