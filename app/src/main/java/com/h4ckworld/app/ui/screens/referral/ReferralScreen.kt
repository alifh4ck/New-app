package com.h4ckworld.app.ui.screens.referral

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.h4ckworld.app.viewmodel.ReferralUiState
import com.h4ckworld.app.viewmodel.ReferralViewModel

@Composable
fun ReferralScreen(
    uid: String,
    viewModel: ReferralViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(uid) {
        viewModel.loadReferralStats(uid)
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Referrals", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is ReferralUiState.Loading -> CircularProgressIndicator()
            is ReferralUiState.Error -> Text("Error: ${state.message}")
            is ReferralUiState.Loaded -> {
                Text("Your code: ${state.stats.referralCode}")
                Text("Total referrals: ${state.stats.totalReferrals}")
                Text("Earned from referrals: ৳ ${state.stats.earnedForReferrer}")
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    clipboard.setText(AnnotatedString(state.stats.referralCode))
                }) {
                    Text("Copy referral code")
                }
            }
        }
    }
}
