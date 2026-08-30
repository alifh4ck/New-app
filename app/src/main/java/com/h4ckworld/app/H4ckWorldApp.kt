package com.h4ckworld.app

import android.app.Application

class H4ckWorldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase auto-initializes via the google-services.json you add yourself.
    }
}
