# H4CK WORLD — starter scaffold

A from-scratch Kotlin + Jetpack Compose starter for a referral / "watch ads & earn
credits" style app. Same *feature set* as the kind of app you showed me (referral
system, CPM ad view, wallet/earnings, Firebase push) — but written clean, with your
own package name and no reused code, keys, or backend.

## What's here
- `app/build.gradle.kts` — Compose, Retrofit, Firebase (Auth/Messaging/Analytics) deps
- `MainActivity.kt` + simple Compose Navigation (Home → Ad → Referral)
- `HomeScreen.kt` — wallet/earnings summary, "Watch & Earn" button
- `AdScreen.kt` — WebView that loads a CPM smartlink URL, with a dwell timer
- `ReferralScreen.kt` — referral code + stats
- `api/` — Retrofit interface + models for a backend you build yourself
- `viewmodel/` — StateFlow-based ViewModels for wallet + referrals
- `fcm/H4ckWorldFcmService.kt` — push notification stub

## What you still need to do
1. **Create your own Firebase project** at console.firebase.google.com, download
   `google-services.json`, and drop it in `app/`. Don't reuse anyone else's.
2. **Build your own backend.** The endpoints in `api/BackendApi.kt` are a contract,
   not a live server — stand up something (Firebase Cloud Functions, a small
   Node/Express app on Render/Railway, or Supabase Edge Functions) that owns its
   own user + credit database, and implements the crediting rules (rate limits,
   fraud checks) server-side. **Never trust the client to decide reward amounts.**
3. **Sign up for a CPM/ad network as a publisher** (AdMob rewarded ads is the
   cleanest fit for "watch ad → earn" flows; Adsterra/Monetag/PropellerAds work as
   smartlinks like `AdManager.kt` expects) and put your own smartlink/App ID in
   `BuildConfig.ADSTERRA_SMARTLINK_URL` in `app/build.gradle.kts`.
4. **Build a real login flow** — Firebase Auth (Google/phone/email) on the client,
   then exchange the Firebase ID token for your own backend JWT via
   `BackendApi.oauthLogin()`. This scaffold leaves `bearerToken` blank in
   `MainActivity.kt` on purpose.
5. Open in Android Studio, let Gradle sync (needs internet), run.

## Before you publish to Play Store, know this
- Google Play's Developer Program Policy restricts **"incentivized" traffic** —
  paying users to click ads, visit sites, or watch ads purely to generate ad
  revenue is a common ban reason. Read the "Ads" and "Deceptive Behavior" sections
  of the policy before launch.
- Don't request `REQUEST_INSTALL_PACKAGES` unless you have a real, disclosed
  reason — Play Console will flag it for review on earning-style apps.
- CPM rates for South Asian traffic are typically low (roughly $0.50–$2 per 1000
  impressions depending on network/geo), so keep payout promises realistic.
- Verify claims/rewards server-side, not just on-device — client-side "the user
  watched an ad" checks are trivially fakeable.
