package com.h4ckworld.app.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

data class WalletData(
    val totalEarned: Double = 0.0,
    val todayEarnings: Double = 0.0,
    val weekEarnings: Double = 0.0,
    val monthEarnings: Double = 0.0,
    val pendingWithdrawal: Double = 0.0
)

data class ReferralData(
    val referralCode: String = "",
    val totalReferrals: Long = 0,
    val earnedForReferrer: Double = 0.0,
    val expPerReferral: Int = 10
)

/**
 * Talks to Firestore directly from the app (no custom server) — free on
 * Firebase's Spark plan, no card required. Anti-abuse (claim cooldown, one
 * referral bonus per new user) is enforced by firestore.rules on the backend
 * project, not just trusted client-side — see H4CKWORLD-backend/firestore.rules.
 */
class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val REWARD_PER_AD_CLAIM = 0.5
    private val REFERRAL_BONUS_TO_REFERRER = 2.0
    private val EXP_PER_REFERRAL = 10

    /** Call once after Firebase sign-in. Creates the user's doc if it doesn't exist yet. */
    suspend fun ensureUserDoc(uid: String, referredByCode: String? = null) {
        val userRef = db.collection("users").document(uid)
        val snap = userRef.get().await()
        if (snap.exists()) return

        val referralCode = uid.take(6).uppercase() + Random.nextInt(1000, 9999)
        val newUser = hashMapOf(
            "uid" to uid,
            "referralCode" to referralCode,
            "totalEarned" to 0.0,
            "todayEarnings" to 0.0,
            "weekEarnings" to 0.0,
            "monthEarnings" to 0.0,
            "pendingWithdrawal" to 0.0,
            "totalReferrals" to 0L,
            "earnedForReferrer" to 0.0,
            "lastClaimAt" to null,
            "createdAt" to FieldValue.serverTimestamp()
        )
        userRef.set(newUser).await()

        if (!referredByCode.isNullOrBlank()) {
            val referrerQuery = db.collection("users")
                .whereEqualTo("referralCode", referredByCode)
                .limit(1)
                .get()
                .await()

            val referrerDoc = referrerQuery.documents.firstOrNull()
            if (referrerDoc != null) {
                referrerDoc.reference.update(
                    mapOf(
                        "totalReferrals" to FieldValue.increment(1),
                        "earnedForReferrer" to FieldValue.increment(REFERRAL_BONUS_TO_REFERRER),
                        "totalEarned" to FieldValue.increment(REFERRAL_BONUS_TO_REFERRER),
                        "todayEarnings" to FieldValue.increment(REFERRAL_BONUS_TO_REFERRER)
                    )
                ).await()

                db.collection("referrals").add(
                    mapOf(
                        "referrerId" to referrerDoc.id,
                        "referredUserId" to uid,
                        "expAwarded" to EXP_PER_REFERRAL,
                        "bonusAwarded" to REFERRAL_BONUS_TO_REFERRER,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        }
    }

    suspend fun getWallet(uid: String): WalletData {
        val snap = db.collection("users").document(uid).get().await()
        return WalletData(
            totalEarned = snap.getDouble("totalEarned") ?: 0.0,
            todayEarnings = snap.getDouble("todayEarnings") ?: 0.0,
            weekEarnings = snap.getDouble("weekEarnings") ?: 0.0,
            monthEarnings = snap.getDouble("monthEarnings") ?: 0.0,
            pendingWithdrawal = snap.getDouble("pendingWithdrawal") ?: 0.0
        )
    }

    suspend fun getReferralStats(uid: String): ReferralData {
        val snap = db.collection("users").document(uid).get().await()
        return ReferralData(
            referralCode = snap.getString("referralCode") ?: "",
            totalReferrals = snap.getLong("totalReferrals") ?: 0L,
            earnedForReferrer = snap.getDouble("earnedForReferrer") ?: 0.0,
            expPerReferral = EXP_PER_REFERRAL
        )
    }

    /**
     * Claims the "watch & earn" reward. The 5-minute cooldown is enforced by
     * firestore.rules server-side (see resource.data.lastClaimAt check there) —
     * this call will fail with a permission error if the cooldown hasn't passed,
     * so a modified/fake client can't bypass it.
     */
    suspend fun claimReward(uid: String): Result<Double> {
        return try {
            val userRef = db.collection("users").document(uid)
            userRef.update(
                mapOf(
                    "totalEarned" to FieldValue.increment(REWARD_PER_AD_CLAIM),
                    "todayEarnings" to FieldValue.increment(REWARD_PER_AD_CLAIM),
                    "weekEarnings" to FieldValue.increment(REWARD_PER_AD_CLAIM),
                    "monthEarnings" to FieldValue.increment(REWARD_PER_AD_CLAIM),
                    "lastClaimAt" to FieldValue.serverTimestamp()
                )
            ).await()

            db.collection("claims").add(
                mapOf(
                    "userId" to uid,
                    "amount" to REWARD_PER_AD_CLAIM,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(REWARD_PER_AD_CLAIM)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerFcmToken(uid: String, token: String) {
        db.collection("users").document(uid).update("fcmToken", token).await()
    }

    /**
     * Submits a claim that the user completed an offerwall task (app install,
     * survey, etc). This does NOT credit the balance automatically — an admin
     * reviews it in the Admin Panel and approves/rejects it. This is the
     * "manual crediting" mode: safe, free (no server webhook needed), and
     * fully legitimate since real third-party offers with real user actions
     * are what's being rewarded.
     */
    suspend fun submitOfferCompletion(uid: String, offerName: String, note: String) {
        db.collection("offerSubmissions").add(
            mapOf(
                "userId" to uid,
                "offerName" to offerName,
                "note" to note,
                "status" to "pending",
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }
}
