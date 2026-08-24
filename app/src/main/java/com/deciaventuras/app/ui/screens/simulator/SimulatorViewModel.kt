package com.deciaventuras.app.ui.screens.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deciaventuras.app.domain.model.Choice
import com.deciaventuras.app.domain.model.Dilemma
import com.deciaventuras.app.domain.repository.DilemmaRepository
import com.deciaventuras.app.domain.repository.UserPreferencesRepository
import com.deciaventuras.app.domain.usecase.RecordChoiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SimulatorUiState(
    val dilemma: Dilemma? = null,
    val choices: List<Choice> = emptyList(),
    /** No nulo únicamente cuando ya se soltó una tarjeta: dispara la vista de consecuencias. */
    val resultChoice: Choice? = null,
    val isLoading: Boolean = true,
    val hapticsEnabled: Boolean = true,
    /** Id de la fila de progreso recién guardada, para poder adjuntarle la reflexión del niño. */
    val savedProgressId: Int? = null,
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
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val dilemma = repository.getDilemma(dilemmaId)
            repository.observeChoices(dilemmaId).collect { choiceList ->
                _uiState.value = _uiState.value.copy(
                    dilemma = dilemma,
                    choices = choiceList.sortedBy { it.orderIndex },
                    isLoading = false,
                )
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.observePreferences().collect { prefs ->
                _uiState.value = _uiState.value.copy(hapticsEnabled = prefs.hapticsEnabled)
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

            // RecordChoiceUseCase no expone el id de la fila que insertó (no
            // es su responsabilidad); se busca la entrada recién guardada
            // que coincide con este dilema y esta decisión, quedándose con
            // la más reciente por si es una repetición (rejugar un dilema
            // ya completado).
            val progressId = repository.observeProgress().first()
                .filter { it.dilemmaId == dilemmaId && it.chosenChoiceId == choice.id }
                .maxByOrNull { it.timestampMillis }
                ?.id

            _uiState.value = _uiState.value.copy(savedProgressId = progressId)
        }
    }

    /**
     * Se llama al presionar "Guardar en mi Diario y Continuar". Si el niño
     * escribió una reflexión, se adjunta a la decisión ya guardada; si la
     * dejó vacía, simplemente se continúa (la reflexión es siempre opcional).
     */
    fun saveReflectionAndContinue(reflection: String, onDone: () -> Unit) {
        val progressId = _uiState.value.savedProgressId
        val trimmed = reflection.trim()
        if (progressId != null && trimmed.isNotEmpty()) {
            viewModelScope.launch {
                repository.updateReflection(progressId, trimmed)
                onDone()
            }
        } else {
            onDone()
        }
    }
}
