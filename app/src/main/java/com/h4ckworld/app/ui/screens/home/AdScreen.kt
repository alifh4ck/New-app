package com.h4ckworld.app.ui.screens.home

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.h4ckworld.app.ads.AdManager

/**
 * Shows the CPM smartlink in a WebView. After MIN_DWELL_TIME_MS has passed,
 * calls onEligibleForClaim() so the caller can ask the backend to mint/validate
 * a claim key. The dwell timer is a basic anti-fraud measure only — real fraud
 * prevention (device checks, rate limiting, IP checks) belongs server-side.
 */
@Composable
fun AdScreen(onEligibleForClaim: () -> Unit) {
    var elapsedMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(AdManager.MIN_DWELL_TIME_MS)
        elapsedMs = AdManager.MIN_DWELL_TIME_MS
        onEligibleForClaim()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(AdManager.getSmartlinkUrl())
            }
        }
    )
}
