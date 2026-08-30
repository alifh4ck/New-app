package com.h4ckworld.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h4ckworld.app.api.ApiClient
import com.h4ckworld.app.api.ReferralStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReferralUiState {
    object Loading : ReferralUiState()
    data class Loaded(val stats: ReferralStats) : ReferralUiState()
    data class Error(val message: String) : ReferralUiState()
}

class ReferralViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ReferralUiState>(ReferralUiState.Loading)
    val uiState: StateFlow<ReferralUiState> = _uiState

    fun loadReferralStats(bearerToken: String) {
        viewModelScope.launch {
            _uiState.value = ReferralUiState.Loading
            try {
                val response = ApiClient.backend.getReferralStats("Bearer $bearerToken")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ReferralUiState.Loaded(response.body()!!)
                } else {
                    _uiState.value = ReferralUiState.Error("Failed to load referral stats (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = ReferralUiState.Error(e.message ?: "Network error")
            }
        }
    }
}
