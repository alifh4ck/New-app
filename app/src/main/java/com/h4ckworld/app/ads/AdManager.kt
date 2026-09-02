package com.h4ckworld.app.ads

/**
 * A rotation of your Adsterra (or other network) smartlink URLs. Put as many
 * as you have — the Ad screen shows them ONE AT A TIME, fully visible on
 * screen, for AD_DWELL_TIME_MS each, then automatically moves to the next.
 * Nothing loads in the background or off-screen — that's what would count as
 * invalid traffic to the ad network. Only what's genuinely on screen counts.
 *
 * TODO: replace these placeholders with your real Adsterra smartlink URLs.
 */
object AdManager {

    val adUrls: List<String> = listOf(
        com.h4ckworld.app.BuildConfig.ADSTERRA_SMARTLINK_URL
        // "https://your-second-adsterra-link",
        // "https://your-third-adsterra-link",
    )

    /** How long each individual ad stays visible on screen before auto-advancing. */
    const val AD_DWELL_TIME_MS = 15000L

    /**
     * How many ads to auto-play in one sitting before returning the user to
     * Home. Keep this reasonable — a user genuinely watching 20 ads back to
     * back without any break is itself a pattern ad networks may flag, so
     * don't set this too high.
     */
    const val ADS_PER_SESSION = 5
}
