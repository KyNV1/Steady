---
phase: 3
title: "Onboarding, authentication and initial profile"
status: implemented — build/device verification pending
priority: P1
dependencies: [1, 2]
effort: "4-5 days"
---

# Phase 3: Onboarding, Authentication and Initial Profile

## Overview

Complete first-run routing and replace the starter flow with production-ready Google sign-in plus medication/goal setup.

## Related Code Files

- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/auth/signin/*`
- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/splash/*`
- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/repository/FirebaseAuthBackend.kt`
- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/core/storage/AppDataStore.kt`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/onboarding/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/medication/*`
- Create: `D:/chplay/Steady/app/src/test/java/com/steady/app/feature/auth/*`

## Implementation Steps

1. Persist onboarding completion separately from auth/profile completion.
2. Implement three Stitch onboarding pages and Skip/Next/Get started semantics.
3. Complete Credential Manager Google ID flow, cancellation/error recovery and Firebase token exchange.
4. Route first-time authenticated users through medication and recommended-goal setup; returning complete profiles go Home.
5. Keep fake auth debug-only and make release fail clearly when Firebase/OAuth configuration is missing.
6. Test state matrix: first install, skipped onboarding, cancelled sign-in, returning user, incomplete setup, expired session.

## Success Criteria

- [ ] First-run and returning-user routes are deterministic after process death.
- [ ] Release build cannot silently use fake authentication.
- [ ] Sign-in cancellation is non-fatal and retryable.
- [ ] Medication type/frequency/dose and goals are available to downstream features.

## Risk Assessment

OAuth configuration differs between debug and release signing keys. Register both SHA fingerprints and test an internal Play build, not only a locally installed APK.

## Completion Notes (2026-09-28)

Google Sign-In plumbing (Credential Manager + Firebase token exchange in
`FirebaseAuthBackend`) turned out to already be functionally complete from
earlier work; this pass focused on the actual gaps: onboarding visual fidelity,
first-run routing state, and the medication/goals setup flow.

**Routing.** `SplashViewModel` is now the single router, combining
`AuthRepository.isAuthenticated`, a new `AppPreferences.onboardingCompleted`
flag, and `TrackingRepository.observeMedicationProfile(userId)` into
`SplashDestination { ONBOARDING, SIGN_IN, PROFILE_SETUP, HOME }`. Post
sign-in, `SignInViewModel` does its own one-shot profile check (via the
session's `user.id`) and reports `PostAuthDestination` directly rather than
bouncing back through Splash — avoids back-stack duplication. New top-level
routes `MedicationSetupRoute` → `GoalsSetupRoute` sit between `SignInRoute`
and `AuthGraphRoute`; both use `popUpTo(navController.graph.id){inclusive=true}`
when landing in the authenticated area, which reliably clears the stack
regardless of which path (first-run vs returning) got the user there.
`OnboardingRoute` is no longer popped when navigating to `SignInRoute` — it's
pushed instead, so Sign-in's "Cancel" can pop back to the last onboarding page.

**Onboarding screens.** Rebuilt against the Stitch designs (project
`13915625241051555386`): each of the 3 pages now has a hero visual (dashboard
mock / scan tag / trend card), a 3-dot page indicator, a brand top bar on page
1 and a back-chevron on pages 2–3, an arrow icon on the Next/Get started
button, and an "Already have an account? Sign in" link. Copy strings were
left as-is (already localized, semantically equivalent to the Stitch text)
rather than rewritten word-for-word.

**Medication & goals setup.** `MedicationDoseScreen`/`DailyGoalsScreen` (the
Profile-tab settings screens) now have real forms instead of
`ComingSoonContent`, and `MedicationSetupScreen`/`GoalsSetupScreen` reuse the
same form composables/ViewModels for the first-run flow (different chrome +
CTA, same underlying state). Daily goals default to the Stitch mock's
recommended values (25g fiber / 70g protein / 64oz water).

**`TrackingRepositoryImpl` is intentionally partial.** Per a scoping decision
made with the user, only `observeMedicationProfile`/`saveMedicationProfile`/
`observeDailyGoals`/`saveDailyGoals` are real (backed by two new Room tables,
`AppDatabase` bumped to v2 with an additive migration). The other 9 interface
methods (`observeMeals`, `saveDose`, `clearUserData`, etc.) are `TODO()` —
nothing calls them yet; see the note added to `phase-04-core-health-tracking.md`.

**Not done / deferred:** the real Google "G" logo asset (button currently
uses a plain `OutlinedButton`, not a pixel-match of the Stitch bottom-sheet
button), a live Terms/Privacy Policy link (plain static text — URLs aren't
finalized per this plan's own Open Questions), and TalkBack/manual
accessibility review (same constraint as Phase 2 — needs a physical
device/manual pass).

**Verified in this session:** `./gradlew compileDebugKotlin`,
`testDebugUnitTest` (all green, including new `SplashViewModelTest`,
`SignInViewModelTest`, `MedicationDoseViewModelTest`, `DailyGoalsViewModelTest`,
extended `AppPreferencesSerializerTest`), `lintDebug` (clean), `assembleDebug`,
and `bundleRelease` (R8 minification succeeds with the new Hilt modules).
**Not verified here:** the new instrumented `TrackingRepositoryImplTest`
(compiles, but needs `connectedDebugAndroidTest` on a device/emulator — left
for the user to run) and the manual on-device state-matrix walkthrough
described in the plan's Verification section.
