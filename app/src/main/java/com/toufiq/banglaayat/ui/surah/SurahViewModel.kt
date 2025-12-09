package com.toufiq.banglaayat.ui.surah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toufiq.banglaayat.data.common.Result
import com.toufiq.banglaayat.data.model.Surah
import com.toufiq.banglaayat.data.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SurahUiState(
    val isLoading: Boolean = false,
    val surah: Surah? = null,
    val error: String? = null
)

@HiltViewModel
class SurahViewModel @Inject constructor(
    private val repository: SurahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurahUiState())
    val uiState: StateFlow<SurahUiState> = _uiState.asStateFlow()

    fun loadSurah(surahNumber: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = repository.getSurah(surahNumber)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, surah = result.data) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Result.Loading -> { /* Already handled above */ }
            }
        }
    }
} 