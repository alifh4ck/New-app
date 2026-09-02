package com.h4ckworld.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h4ckworld.app.data.FirestoreRepository
import com.h4ckworld.app.data.ReferralData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReferralUiState {
    object Loading : ReferralUiState()
    data class Loaded(val stats: ReferralData) : ReferralUiState()
    data class Error(val message: String) : ReferralUiState()
}

class ReferralViewModel : ViewModel() {

    private val repo = FirestoreRepository()
    private val _uiState = MutableStateFlow<ReferralUiState>(ReferralUiState.Loading)
    val uiState: StateFlow<ReferralUiState> = _uiState

    fun loadReferralStats(uid: String) {
        viewModelScope.launch {
            _uiState.value = ReferralUiState.Loading
            try {
                _uiState.value = ReferralUiState.Loaded(repo.getReferralStats(uid))
            } catch (e: Exception) {
                _uiState.value = ReferralUiState.Error(e.message ?: "Failed to load referral stats")
            }
        }
    }
}
