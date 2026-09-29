---
phase: 4
title: "Core health tracking"
status: pending
priority: P1
dependencies: [1, 2]
effort: "7-9 days"
---

# Phase 4: Core Health Tracking

## Overview

Implement offline-first Room storage and the Home, Daily Goals, Medication, Dose and Side Effects workflows.

> **Note (2026-09-28, from Phase 3):** the `MedicationProfile`/`DailyGoals` slice of this
> phase's Room schema was already built in Phase 3 (needed for first-run setup routing) —
> `MedicationProfileEntity`/`DailyGoalsEntity`, their DAOs, `AppDatabase` v2 migration, and
> the corresponding 4 `TrackingRepositoryImpl` methods. Real settings screens
> (`MedicationDoseScreen`, `DailyGoalsScreen`) and their ViewModels also already exist. This
> phase's remaining scope is: `MealLogEntity`/`DoseLogEntity`/`SideEffectLogEntity`/
> `ProgressEntryEntity` + DAOs, the rest of `TrackingRepositoryImpl` (currently `TODO()`),
> Home's reactive cards/quick-add, next-dose/streak calculation, Log Dose/Side Effect
> screens, and the DST/migration tests. Don't redo the medication/goals persistence.

## Architecture

Use normalized Room entities with UTC instants plus user timezone/local day keys. Repository transactions update logs and derived daily totals atomically. UI applies optimistic quick-add then rolls back on failure.

## Related Code Files

- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/core/storage/AppDatabase.kt`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/local/entity/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/local/dao/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/repository/TrackingRepositoryImpl.kt`
- Replace: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/home/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/goals/*`, `medication/*`, `dose/*`, `sideeffects/*`
- Create: Room/repository/ViewModel tests under `D:/chplay/Steady/app/src/test/`

## Implementation Steps

1. Add entities/DAOs and a versioned Room migration; never use destructive migration for user health logs.
2. Implement daily totals, next-dose calculation and streak rules with injectable clock/timezone.
3. Build Home cards and quick-add water/fiber/protein.
4. Build Daily Goals and Medication & Dose settings; injection site appears only for injection medication.
5. Build Log Dose and Side Effects; severe severity triggers a non-diagnostic urgent-care warning.
6. Add idempotency rules to prevent duplicate rapid taps/logs and tests across DST/midnight.

## Success Criteria

- [ ] Logs and goals work offline and survive restart.
- [ ] Home totals and next-dose card update reactively after each save.
- [ ] Injection-site UI cannot persist for oral medication.
- [ ] Severe symptoms always display approved escalation copy.
- [ ] Room migration and timezone boundary tests pass.

## Risk Assessment

Health records are sensitive and date logic is failure-prone. Exclude DB/photos from Android backup until encrypted sync/retention is designed; test DST and locale-independent units.
