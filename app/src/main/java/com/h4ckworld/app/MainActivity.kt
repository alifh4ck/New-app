package com.h4ckworld.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.h4ckworld.app.ui.screens.home.AdScreen
import com.h4ckworld.app.ui.screens.home.HomeScreen
import com.h4ckworld.app.ui.screens.referral.ReferralScreen
import com.h4ckworld.app.ui.theme.H4ckWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            H4ckWorldTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()

    // TODO: replace with a real signed-in JWT from your auth flow (Firebase Auth ->
    // your backend's /api/auth/oauth exchange). Left blank here since login UI is
    // intentionally not scaffolded — wire up FirebaseAuth however you prefer.
    var bearerToken by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                bearerToken = bearerToken,
                onWatchAdClick = { navController.navigate("ad") }
            )
        }
        composable("ad") {
            AdScreen(onEligibleForClaim = {
                // TODO: call ApiClient.backend.claimReward() with a claim key
                // your backend generates for this session, then navController.popBackStack()
            })
        }
        composable("referral") {
            ReferralScreen(bearerToken = bearerToken)
        }
    }
}
