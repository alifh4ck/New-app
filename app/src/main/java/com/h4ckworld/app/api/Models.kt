package com.h4ckworld.app.api

data class AuthResponse(
    val jwtToken: String,
    val refreshToken: String,
    val userId: String,
    val referralCode: String
)

data class ClaimRequest(
    val claimKey: String
)

data class ClaimResponse(
    val success: Boolean,
    val creditsAwarded: Double,
    val newBalance: Double
)

data class WalletResponse(
    val totalEarned: Double,
    val todayEarnings: Double,
    val weekEarnings: Double,
    val monthEarnings: Double,
    val pendingWithdrawal: Double
)

data class ReferralStats(
    val referralCode: String,
    val totalReferrals: Int,
    val earnedForReferrer: Double,
    val expPerReferral: Int
)

data class RegisterFcmTokenRequest(
    val fcmToken: String
)
