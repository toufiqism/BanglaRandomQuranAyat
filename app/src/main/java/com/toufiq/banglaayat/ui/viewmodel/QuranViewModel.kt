package com.toufiq.banglaayat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toufiq.banglaayat.data.model.QuranResponse
import com.toufiq.banglaayat.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuranUiState>(QuranUiState.Initial)
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    fun loadQuranAyah(surah: Int, ayah: Int) {
        viewModelScope.launch {
            _uiState.value = QuranUiState.Loading
            repository.getQuranAyah(surah, ayah)
                .onSuccess { response ->
                    _uiState.value = QuranUiState.Success(response)
                }
                .onFailure { error ->
                    _uiState.value = QuranUiState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }
}

sealed class QuranUiState {
    object Initial : QuranUiState()
    object Loading : QuranUiState()
    data class Success(val data: QuranResponse) : QuranUiState()
    data class Error(val message: String) : QuranUiState()
} 