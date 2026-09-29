---
phase: 5
title: "AI meal scan and food log"
status: pending
priority: P1
dependencies: [1, 2, 4]
effort: "7-10 days"
---

# Phase 5: AI Meal Scan and Food Log

## Overview

Deliver the product's core CameraX → protected Gemini analysis → editable result → Room log flow.

## Architecture

Capture/compress/rotate locally, upload a bounded image to an authenticated callable Function, validate structured response server-side and client-side, then persist only after user confirmation. Quota is server-enforced per UID/day; the client only presents status.

## Related Code Files

- Modify: `D:/chplay/Steady/app/src/main/AndroidManifest.xml`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/scan/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/mealresult/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/repository/MealScanRepositoryImpl.kt`
- Create: `D:/chplay/Steady/functions/src/analyze-meal.ts`
- Create: contract/backend tests in `D:/chplay/Steady/functions/test/` and Android repository/ViewModel tests.

## Implementation Steps

1. Implement runtime camera permission states and CameraX lifecycle-safe preview/capture.
2. Normalize orientation, cap dimensions/bytes, strip unnecessary EXIF and delete temporary images.
3. Benchmark candidate Gemini models on representative small GLP-1 portions; lock a versioned structured schema and prompt.
4. Require Auth/App Check, MIME/size validation, timeout, daily quota and server-side secret access.
5. Render loading, timeout, no-food, quota and retry states without duplicate billing/API calls.
6. Build result/edit confirmation for food name, portion and macros; save meal and update Home totals atomically.
7. Add contract fixtures for malformed, partial, extreme and multi-food responses.

## Success Criteria

- [ ] No Gemini credential is present in source, BuildConfig, logs or APK resources.
- [ ] A successful scan can be edited and appears in Home/Insights after restart.
- [ ] Quota and idempotency are enforced server-side.
- [ ] Permission denial, rotation, offline mode, timeout and invalid AI JSON have recoverable UX.
- [ ] Real-photo benchmark meets an agreed accuracy/error threshold before release.

## Risk Assessment

AI nutrition values are estimates. Always show editable values and disclaimer; log model/schema version, latency and coarse error codes without retaining photos or health content in analytics.
