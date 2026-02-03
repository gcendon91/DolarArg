package com.gcendon.dolararg.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    init {
        fetchDolares()
    }

    fun fetchDolares() {
        viewModelScope.launch {
            _uiState.value = DolarUiState.Loading
            try {
                val dolares = repository.getDolares()
                _uiState.value = DolarUiState.Success(dolares)
            } catch (e: Exception) {
                _uiState.value = DolarUiState.Error("Error de conexión")
            }
        }
    }

    fun retry() = fetchDolares()
}