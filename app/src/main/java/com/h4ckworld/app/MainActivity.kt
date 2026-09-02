package com.h4ckworld.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.h4ckworld.app.data.FirestoreRepository
import com.h4ckworld.app.ui.screens.home.AdScreen
import com.h4ckworld.app.ui.screens.home.HomeScreen
import com.h4ckworld.app.ui.screens.referral.ReferralScreen
import com.h4ckworld.app.ui.theme.H4ckWorldTheme
import com.h4ckworld.app.viewmodel.WalletViewModel
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            H4ckWorldTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

/**
 * First-run: signs the user in anonymously with Firebase Auth (no email/password
 * needed to get started), then creates their Firestore user doc if it's their
 * first time. Once that's done, uid is passed down to every screen.
 */
@Composable
fun AppRoot() {
    var uid by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val repo = remember { FirestoreRepository() }

    LaunchedEffect(Unit) {
        try {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser ?: auth.signInAnonymously().await().user
            val userId = user?.uid ?: throw IllegalStateException("Sign-in failed")

            repo.ensureUserDoc(userId)
            uid = userId
        } catch (e: Exception) {
            errorMessage = e.message ?: "Sign-in failed"
        }
    }

    when {
        errorMessage != null -> Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error: $errorMessage")
        }
        uid == null -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        else -> AppNavHost(uid = uid!!)
    }
}

@Composable
fun AppNavHost(uid: String) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                uid = uid,
                onWatchAdClick = { navController.navigate("ad") }
            )
        }
        composable("ad") {
            val walletViewModel: WalletViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            AdScreen(onEligibleForClaim = {
                walletViewModel.claimReward(uid) { _, _ ->
                    navController.popBackStack()
                }
            })
        }
        composable("referral") {
            ReferralScreen(uid = uid)
        }
    }
}
