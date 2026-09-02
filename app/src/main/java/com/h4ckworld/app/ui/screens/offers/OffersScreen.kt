package com.h4ckworld.app.ui.screens.offers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.h4ckworld.app.data.FirestoreRepository
import kotlinx.coroutines.launch

/**
 * Offerwall screen. Real third-party offers (app installs, surveys, etc) from
 * AdGate Media — much higher payout per completion than a single ad view,
 * and fully legitimate since users genuinely complete the offer requirements.
 *
 * Crediting is manual for now (see FirestoreRepository.submitOfferCompletion):
 * the user submits which offer they completed, and it shows up in the Admin
 * Panel's "Pending offer submissions" list for you to approve/reject. This
 * avoids needing a paid server webhook while still being a real, legal
 * rewards flow.
 */
@Composable
fun OffersScreen(uid: String) {
    val repo = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()

    var offerName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var submittedMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Offers", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Complete real offers (app installs, surveys, sign-ups) from our " +
                "partner network for much bigger rewards than a single ad view.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // TODO: once you have an AdGate Media publisher account, wire
                // up their Android SDK here to open the real offer wall, e.g.:
                //
                // AdGateMedia.getInstance().loadOfferWall(
                //     activity, wallCode, uid, subIds,
                //     onSuccess = { AdGateMedia.getInstance().showOfferWall(activity) },
                //     onFailure = { }
                // )
                //
                // See: https://github.com/adgatemedia/adgate-rewards-android-sdk
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Offer Wall")
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        Text("Completed an offer? Submit it for review:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = offerName,
            onValueChange = { offerName = it },
            label = { Text("Offer name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Notes (optional — e.g. account used, screenshot taken)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Button(
            enabled = offerName.isNotBlank() && !submitting,
            onClick = {
                submitting = true
                scope.launch {
                    repo.submitOfferCompletion(uid, offerName, note)
                    submitting = false
                    submittedMessage = "Submitted! An admin will review and credit your balance soon."
                    offerName = ""
                    note = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (submitting) "Submitting..." else "Submit for review")
        }

        submittedMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodySmall)
        }
    }
}
