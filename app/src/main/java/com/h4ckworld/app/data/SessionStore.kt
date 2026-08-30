package com.h4ckworld.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {

    private val jwtKey = stringPreferencesKey("jwt_token")
    private val refreshKey = stringPreferencesKey("refresh_token")
    private val referralCodeKey = stringPreferencesKey("referral_code")

    val jwtToken: Flow<String?> = context.dataStore.data.map { it[jwtKey] }
    val referralCode: Flow<String?> = context.dataStore.data.map { it[referralCodeKey] }

    suspend fun saveSession(jwt: String, refresh: String, referralCode: String) {
        context.dataStore.edit {
            it[jwtKey] = jwt
            it[refreshKey] = refresh
            it[referralCodeKey] = referralCode
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
