---
title: Steady Android MVP implementation
description: >-
  Build the validated Android MVP from the current AppBase skeleton, the
  19-screen Stitch design, and the P0 product checklist.
status: in-progress
priority: P1
branch: unknown (Git safe-directory check blocked)
tags:
  - feature
  - android
  - frontend
  - backend
  - database
  - auth
  - critical
blockedBy: []
blocks: []
created: 2026-09-27T00:00:00.000Z
createdBy: 'ck:plan'
source: skill
---

# Steady Android MVP Implementation

## Overview

Turn the existing Kotlin/Compose starter into the first releasable Steady Android app. The critical path is onboarding → Google sign-in → medication setup → daily tracking → CameraX/Gemini food scan → local log/insights → verified Google Play subscription. Room is the offline source of truth for personal logs; Firebase provides identity, callable backend functions, private media, entitlement verification, and later community sync.

## Scope

- MVP/P0: 19 designed destinations, local tracking, AI meal scan, medication/dose, side effects, goals, insights, paywall, private Journey storage, account deletion, release hardening.
- P1 after MVP gate: reminder delivery, editable AI result, richer insights, full Community posting/comments/moderation.
- Deferred: AdMob, Health Connect, AI coach, PDF report, pharmacokinetic charts, monthly insight filters.
- Community clarification: deploy deny-by-default Firestore rules in MVP; do not ship a writable feed before rules, abuse limits, report flow, and moderation exist.

## Architecture Decisions

- Keep the single Android app module and current Clean Architecture package boundaries; split by feature, not Gradle modules, until build/runtime evidence justifies modularization.
- Room owns offline health and meal records. Firestore is not a second writable source for those records in MVP.
- The app never contains a Gemini key or trusts a local premium flag. Authenticated/App-Check-protected Cloud Functions proxy AI requests and verify Play purchase tokens.
- Use typed Navigation Compose routes. A root graph handles splash/onboarding/auth/setup; an authenticated graph hosts the five-tab shell and detail forms.
- Medical copy is informational. Severe side effects show a prominent care escalation message and never claim diagnosis.

## Delivery Order

Phases 1–2 establish contracts and navigation. Phase 3 can then run alongside the Room schema portion of phase 4. Phases 4–5 feed phase 6. Phase 7 contains P0 account/privacy work plus P1 community/reminders. Phase 8 is the release gate.

## Phases

| Phase | Name | Status |
|-------|------|--------|
| 1 | [architecture-and-contracts](./phase-01-architecture-and-contracts.md) | Completed |
| 2 | [design-system-and-navigation](./phase-02-design-system-and-navigation.md) | Completed |
| 3 | [onboarding-auth-and-profile](./phase-03-onboarding-auth-and-profile.md) | Implemented — device verification pending |
| 4 | [core-health-tracking](./phase-04-core-health-tracking.md) | Pending |
| 5 | [ai-meal-scan-and-food-log](./phase-05-ai-meal-scan-and-food-log.md) | Pending |
| 6 | [insights-journey-and-subscriptions](./phase-06-insights-journey-and-subscriptions.md) | Pending |
| 7 | [notifications-account-and-community](./phase-07-notifications-account-and-community.md) | Pending |
| 8 | [qa-security-and-play-release](./phase-08-qa-security-and-play-release.md) | Pending |

## Dependencies

- Readiness plan `../260927-2213-steady-implementation-readiness/plan.md` is functionally complete and supplies the auth/dependency baseline; no active blocker.
- External prerequisites: Firebase project/config, OAuth web client ID, deployed Cloud Functions/App Check, Play Console subscription products/testers, privacy policy and medical disclaimer.

## Acceptance Summary

- A new user completes onboarding, signs in with Google, configures medication/goals, and reaches Home.
- Core tracking and the latest daily state work offline and survive process death.
- Food photos are captured, sent only through the protected backend, parsed into a versioned schema, edited/confirmed, and stored locally.
- Premium access comes only from server-verified Play state; locked Journey data stays private.
- Account deletion removes Auth, Firestore and Storage data through an auditable backend job.
- `lintDebug`, unit tests, Compose UI tests, Room migrations, emulator security-rule tests, and `bundleRelease` pass.

## Sources

- Product specs: `../../ke_hoach_app_glp1.xlsx`, `../../steady_app_screen_docs.xlsx`, `../../steady_checklist_tinh_nang.xlsx`
- Google Play Billing: https://developer.android.com/google/play/billing/integrate
- Billing backend verification: https://developer.android.com/google/play/billing/backend
- Firebase callable functions/App Check: https://firebase.google.com/docs/functions/callable
- Gemini image understanding: https://ai.google.dev/gemini-api/docs/image-understanding

## Open Questions

- Confirm production Gemini model and JSON contract after a real-photo benchmark; do not hardcode the planning workbook's model name.
- Confirm weekly/yearly Play product IDs, trial eligibility, price copy, privacy/support URLs, and legal/medical wording before phase 6/8.
- Confirm whether Community must be in first public release. This plan keeps it P1 because the MVP feature sheet explicitly defers social/community.
