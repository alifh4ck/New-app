package com.h4ckworld.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h4ckworld.app.data.FirestoreRepository
import com.h4ckworld.app.data.WalletData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WalletUiState {
    object Loading : WalletUiState()
    data class Loaded(val wallet: WalletData) : WalletUiState()
    data class Error(val message: String) : WalletUiState()
}

class WalletViewModel : ViewModel() {

    private val repo = FirestoreRepository()
    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState: StateFlow<WalletUiState> = _uiState

    fun loadWallet(uid: String) {
        viewModelScope.launch {
            _uiState.value = WalletUiState.Loading
            try {
                _uiState.value = WalletUiState.Loaded(repo.getWallet(uid))
            } catch (e: Exception) {
                _uiState.value = WalletUiState.Error(e.message ?: "Failed to load wallet")
            }
        }
    }

    fun claimReward(uid: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.claimReward(uid)
            result.fold(
                onSuccess = { amount ->
                    onResult(true, "Claimed ৳$amount")
                    loadWallet(uid)
                },
                onFailure = { e ->
                    onResult(false, e.message ?: "Claim failed — please wait a bit and try again")
                }
            )
        }
    }
}
