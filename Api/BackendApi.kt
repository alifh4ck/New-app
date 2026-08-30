package com.h4ckworld.app.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * All endpoints below point at YOUR OWN backend (BuildConfig.BACKEND_BASE_URL).
 * Nothing here talks to any third party's server — you must stand up your own
 * API (e.g. a small Node/Express or Firebase Cloud Functions project) that
 * implements these routes and owns its own user/credit database.
 */
interface BackendApi {

    @POST("api/auth/oauth")
    suspend fun oauthLogin(@Body idToken: Map<String, String>): Response<AuthResponse>

    @GET("api/wallet")
    suspend fun getWallet(@Header("Authorization") bearerToken: String): Response<WalletResponse>

    @GET("api/referral")
    suspend fun getReferralStats(@Header("Authorization") bearerToken: String): Response<ReferralStats>

    @POST("api/auth/claim")
    suspend fun claimReward(
        @Header("Authorization") bearerToken: String,
        @Query("key") claimKey: String
    ): Response<ClaimResponse>

    @POST("api/notifications/register")
    suspend fun registerFcmToken(
        @Header("Authorization") bearerToken: String,
        @Body body: RegisterFcmTokenRequest
    ): Response<Unit>
}
