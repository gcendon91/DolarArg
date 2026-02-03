package com.gcendon.dolararg.ui

import com.gcendon.dolararg.model.Dolar

sealed interface DolarUiState {
    object Loading : DolarUiState
    data class Success(val dolares: List<Dolar>) : DolarUiState
    data class Error(val mensaje: String) : DolarUiState
}