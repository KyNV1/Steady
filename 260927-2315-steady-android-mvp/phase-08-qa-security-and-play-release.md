---
phase: 8
title: "QA, security and Play release"
status: pending
priority: P1
dependencies: [3, 4, 5, 6, 7]
effort: "5-7 days plus beta feedback"
---

# Phase 8: QA, Security and Play Release

## Overview

Prove the complete release path on real devices, Firebase emulators and Play internal testing before exposing health data or paid features.

## Related Code Files

- Modify: `D:/chplay/Steady/app/src/test/**`, `D:/chplay/Steady/app/src/androidTest/**`
- Modify: `D:/chplay/Steady/.github/workflows/android.yml`
- Modify: `D:/chplay/Steady/app/proguard-rules.pro`, `D:/chplay/Steady/app/src/main/AndroidManifest.xml`
- Create: `D:/chplay/Steady/docs/release-checklist.md`, `D:/chplay/Steady/docs/data-safety-inventory.md`

## Implementation Steps

1. Add unit tests for domain calculations, ViewModels, repositories, billing states and AI contract parsing.
2. Add Compose tests for onboarding/auth/setup, tracking, scan/result, paywall and account deletion confirmation.
3. Run Firebase emulator tests for callable auth/App Check behavior, rules and deletion.
4. Test low-memory, rotation, offline/reconnect, denied permissions, large fonts, TalkBack, timezone/DST and slow network.
5. Audit logs, analytics, backups, exported components, secrets, release minification and dependency declarations.
6. Complete privacy policy, medical disclaimer, Data Safety/Health Apps declarations and in-app account-deletion requirements.
7. Run `gradlew lintDebug testDebugUnitTest connectedDebugAndroidTest bundleRelease`; upload signed AAB to internal testing.
8. Validate real Google sign-in, CameraX, AI proxy, purchase/trial/restore/expiry and deletion with tester accounts; fix blockers before staged rollout.

## Success Criteria

- [ ] CI and release bundle pass with no lint/test failures.
- [ ] No secret or sensitive payload appears in logs, Crashlytics breadcrumbs or analytics.
- [ ] Internal Play build completes every critical user journey on supported API levels.
- [ ] Billing, privacy, medical-safety and account-deletion release checks are signed off.
- [ ] Rollback/kill switches exist for AI scan and premium entitlement incidents.

## Risk Assessment

Passing debug tests is insufficient for OAuth, Billing, App Check and minification. Release acceptance requires an internal Play artifact and production-like Firebase configuration.
