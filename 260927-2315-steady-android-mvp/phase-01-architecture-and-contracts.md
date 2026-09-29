---
phase: 1
title: Architecture and contracts
status: completed
priority: P1
dependencies: []
effort: 3-4 days
---

# Phase 1: Architecture and Contracts

## Overview

Freeze the MVP boundary, data ownership and secure client/backend contracts before multiplying screens.

## Requirements

- Define models for medication profile, daily goals, meal log, nutrition totals, dose log, side-effect log, weight/progress photo metadata, notification and entitlement.
- Define repository interfaces and error types without Firebase/Room types leaking into domain.
- Define versioned callable contracts for `analyzeMeal`, `verifyPurchase`, `deleteAccount` and scan quota.
- Add a data classification table: public community content, private profile, sensitive health logs, private progress photos.

## Architecture

`Compose → ViewModel → use case/repository → Room or Firebase adapter`. Room emits flows used by Home and Insights. Callable Functions require Firebase Auth plus App Check and return constrained JSON. Storage paths are UID-scoped.

## Related Code Files

- Modify: `D:/chplay/Steady/app/build.gradle.kts`, `D:/chplay/Steady/gradle/libs.versions.toml`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/domain/model/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/domain/repository/*`
- Create: `D:/chplay/Steady/functions/src/*`, `D:/chplay/Steady/firebase.json`, `D:/chplay/Steady/firestore.rules`, `D:/chplay/Steady/storage.rules`
- Create: `D:/chplay/Steady/docs/decisions/steady-data-ownership.md`

## Implementation Steps

1. Convert the 108-item checklist into P0/P1/P2 scope and map every P0 item to a phase/test.
2. Document entity IDs, units, timestamps, nullable fields, retention and ownership.
3. Specify JSON schemas and failure codes; reject unknown/out-of-range AI values.
4. Add Firebase Functions/App Check/Storage dependencies only where required.
5. Add backend test harness and Firebase emulator configuration.
6. Record decisions for offline ownership, health data isolation, entitlement authority and deletion workflow.

## Success Criteria

- [x] Every P0 checklist row has one implementation owner and validation gate.
- [x] No API secret or premium authority resides in the APK.
- [x] Contracts handle auth failure, quota exceeded, invalid image, malformed AI JSON and network retry.
- [x] Architecture decision documents identify deferred scope explicitly.

## Risk Assessment

Largest risk is building UI before resolving data/server authority. Mitigate with versioned contracts and fake adapters used by previews/tests. Do not introduce multi-module architecture during MVP.
