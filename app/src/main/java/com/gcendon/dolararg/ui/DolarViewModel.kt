package com.gcendon.dolararg.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcendon.dolararg.data.DolarRepository
import com.gcendon.dolararg.ui.DolarUiState
import com.gcendon.dolararg.model.Dolar
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DolarViewModel : ViewModel() {
    // Instanciamos el repositorio a mano
    private val repository = DolarRepository()
    private val _uiState = mutableStateOf<DolarUiState>(DolarUiState.Loading)
    val uiState: State<DolarUiState> = _uiState

    // Nuevo estado para el Pull-to-refresh
    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    var showCalculator by mutableStateOf(false)
        private set

    var amountInput by mutableStateOf("")
        private set

    var isUsdToArs by mutableStateOf(true)
        private set


    init {
        fetchDolares()
    }

    fun fetchDolares() {
        viewModelScope.launch {
            // Si ya estamos refrescando, no mostramos el Loading general (el círculo del medio)
            if (!_isRefreshing.value) _uiState.value = DolarUiState.Loading

            try {
                val dolares = repository.getDolares()
                _uiState.value = DolarUiState.Success(dolares)
            } catch (e: Exception) {
                _uiState.value = DolarUiState.Error("Error de conexión")
            } finally {
                _isRefreshing.value = false // Cortamos la animación de refresco
            }
        }
    }

    fun onRefresh() {
        _isRefreshing.value = true
        fetchDolares()
    }

    fun retry() = fetchDolares()

    fun toggleCalculator() {
        showCalculator = !showCalculator
        // Opcional: Limpiamos el número cuando se cierra para que la próxima vez esté vacío
        if (!showCalculator) amountInput = ""
    }

    fun onAmountChange(input: String) {
        // Validamos que solo entren números, puntos o comas
        if (input.all { it.isDigit() || it == '.' || it == ',' }) {
            amountInput = input.replace(',', '.') // Estandarizamos a punto decimal
        }
    }

    fun toggleDirection() {
        isUsdToArs = !isUsdToArs
    }
}