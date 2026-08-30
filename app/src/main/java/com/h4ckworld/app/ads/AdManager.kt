package com.h4ckworld.app.ads

/**
 * Most CPM ad networks aimed at "earning" apps (Adsterra, Monetag, PropellerAds, etc.)
 * don't ship a native rewarded-ad Android SDK the way AdMob/Unity Ads do — they work
 * as a "smartlink" / "direct link" URL that you open (often in a WebView or Custom Tab),
 * and they pay YOU (the publisher) per impression/click on YOUR account.
 *
 * Important: there is no way to reward an individual user for "watching an ad" unless
 * your own backend decides that, because the ad network only reports aggregate
 * impressions to your publisher dashboard — it does not tell your app "user X watched
 * ad Y, pay them $Z". Any per-user reward amount has to be a rule YOU set on YOUR
 * backend (e.g. "opening the ad screen once per day = X credits"), and you should rate
 * limit / fraud-check it server-side, or the ad network will flag your account for
 * invalid traffic and stop paying out.
 *
 * This class just centralizes the smartlink URL and exposes a simple "was the ad
 * screen opened" callback — the actual crediting decision must happen in your backend
 * (see BackendApi.claimReward), not solely on-device.
 */
object AdManager {

    fun getSmartlinkUrl(): String = com.h4ckworld.app.BuildConfig.ADSTERRA_SMARTLINK_URL

    /**
     * Call this once the WebView/CustomTab showing the smartlink has been open for a
     * minimum dwell time (e.g. 5-10s) to reduce trivial fraud, then ask your backend
     * to mint a one-time claim key and validate/credit it server-side.
     */
    const val MIN_DWELL_TIME_MS = 8000L
}
