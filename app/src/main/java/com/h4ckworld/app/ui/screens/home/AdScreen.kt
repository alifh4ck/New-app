package com.h4ckworld.app.ui.screens.home

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.h4ckworld.app.ads.AdManager

/**
 * Shows the ad rotation ONE AD AT A TIME, always fully visible on screen —
 * never hidden or in the background. After AD_DWELL_TIME_MS the next ad in
 * AdManager.adUrls loads automatically, up to ADS_PER_SESSION ads, then
 * calls onSessionComplete(). No external browser is ever opened — everything
 * stays inside this WebView.
 */
@Composable
fun AdScreen(onSessionComplete: () -> Unit) {
    var adIndex by remember { mutableIntStateOf(0) }
    var progress by remember { mutableIntStateOf(0) }

    val urls = AdManager.adUrls
    val totalAdsThisSession = minOf(AdManager.ADS_PER_SESSION, urls.size).coerceAtLeast(1)

    LaunchedEffect(adIndex) {
        progress = 0
        val stepMs = 200L
        val steps = (AdManager.AD_DWELL_TIME_MS / stepMs).toInt()
        repeat(steps) {
            kotlinx.coroutines.delay(stepMs)
            progress = ((it + 1) * 100 / steps)
        }

        if (adIndex + 1 < totalAdsThisSession) {
            adIndex += 1
        } else {
            onSessionComplete()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Ad ${adIndex + 1} of $totalAdsThisSession",
                style = MaterialTheme.typography.labelLarge
            )
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (urls.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    // key forces WebView to reload when the ad index changes
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            loadUrl(urls[adIndex % urls.size])
                        }
                    },
                    update = { webView ->
                        webView.loadUrl(urls[adIndex % urls.size])
                    }
                )
            } else {
                Text("No ad links configured yet — add some in AdManager.kt")
            }
        }
    }
}
