package com.h4ckworld.app.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class H4ckWorldFcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: send `token` to YOUR backend via BackendApi.registerFcmToken
        // so you can push earnings/milestone notifications to this device.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO: show a notification (earnings update, referral bonus, etc.)
    }
}
