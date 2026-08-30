package com.h4ckworld.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.h4ckworld.app.api.WalletResponse
import com.h4ckworld.app.viewmodel.WalletUiState
import com.h4ckworld.app.viewmodel.WalletViewModel

@Composable
fun HomeScreen(
    bearerToken: String,
    onWatchAdClick: () -> Unit,
    walletViewModel: WalletViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by walletViewModel.uiState.collectAsState()

    LaunchedEffect(bearerToken) {
        walletViewModel.loadWallet(bearerToken)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("H4CK WORLD", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        when (val state = uiState) {
            is WalletUiState.Loading -> CircularProgressIndicator()
            is WalletUiState.Error -> Text("Error: ${state.message}")
            is WalletUiState.Loaded -> WalletSummary(state.wallet)
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = onWatchAdClick, modifier = Modifier.fillMaxWidth()) {
            Text("Watch & Earn")
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Rewards are calculated and verified by our server, not the app itself.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun WalletSummary(wallet: WalletResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text("Total earned", style = MaterialTheme.typography.labelMedium)
            Text("৳ ${wallet.totalEarned}", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column { Text("Today"); Text("৳ ${wallet.todayEarnings}") }
                Column { Text("This week"); Text("৳ ${wallet.weekEarnings}") }
                Column { Text("This month"); Text("৳ ${wallet.monthEarnings}") }
            }
        }
    }
}
