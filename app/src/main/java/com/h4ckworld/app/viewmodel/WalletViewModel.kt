package com.h4ckworld.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h4ckworld.app.api.ApiClient
import com.h4ckworld.app.api.WalletResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WalletUiState {
    object Loading : WalletUiState()
    data class Loaded(val wallet: WalletResponse) : WalletUiState()
    data class Error(val message: String) : WalletUiState()
}

class WalletViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState: StateFlow<WalletUiState> = _uiState

    fun loadWallet(bearerToken: String) {
        viewModelScope.launch {
            _uiState.value = WalletUiState.Loading
            try {
                val response = ApiClient.backend.getWallet("Bearer $bearerToken")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = WalletUiState.Loaded(response.body()!!)
                } else {
                    _uiState.value = WalletUiState.Error("Failed to load wallet (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = WalletUiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun claimReward(bearerToken: String, claimKey: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiClient.backend.claimReward("Bearer $bearerToken", claimKey)
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Claimed ${response.body()!!.creditsAwarded} credits")
                    loadWallet(bearerToken)
                } else {
                    onResult(false, "Claim failed (${response.code()})")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Network error")
            }
        }
    }
}
