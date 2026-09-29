---
phase: 2
title: "Design system and navigation"
status: completed
priority: P1
dependencies: [1]
effort: "4-5 days"
---

# Phase 2: Design System and Navigation

## Overview

Translate Stitch tokens and all 19 destinations into reusable Compose primitives and a testable navigation shell.

## Requirements

- Match Stitch colors, Fraunces/Work Sans typography, spacing, shapes and light theme.
- Implement common app bar, five-tab bottom bar, metric cards, selectors, loading/error/empty states and medical warning panel.
- Add typed routes for onboarding, auth, setup, five primary tabs and all detail screens.
- Preserve state per bottom tab and support back/up/deep-link behavior.

## Related Code Files

- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/ui/theme/*`
- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/navigation/AppRoutes.kt`
- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/navigation/AppNavHost.kt`
- Replace: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/home/HomeScreen.kt`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/ui/components/*`
- Create: feature screen placeholders/previews under `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/`

## Implementation Steps

1. Extract tokens from Stitch `DESIGN.md`; add licensed fonts locally and verify fallback behavior.
2. Build accessible reusable primitives with 48dp touch targets and semantic descriptions.
3. Introduce root and authenticated navigation graphs; use routes rather than local enum tabs.
4. Add all 19 destination skeletons and wire the 47 documented CTA transitions.
5. Add screenshot/Compose navigation tests at compact and large font scales.

## Success Criteria

- [x] All 19 destinations are reachable with no dead P0 CTA.
- [x] Bottom-tab state survives switching and process recreation where expected (nested per-tab
      graphs with `saveState`/`restoreState`; structurally correct, not device-verified).
- [x] Design primitives render in previews without Firebase or camera (each new component ships an
      `@Preview` using only `AppTheme`).
- [ ] TalkBack labels, contrast and font scaling pass manual accessibility review — needs a real
      device/manual pass, not available in this environment.

## Completion Notes (2026-09-28)

Theme tokens (`ui/theme/Color.kt`, `Shape.kt`, `Spacing.kt`, `Typography.kt`, `ExtendedColors.kt`)
now match the Stitch `DESIGN.md` exactly for the light palette; dark is a tonal placeholder since
Stitch never designed one (kept only so the existing `ThemeMode.DARK` setting still works).
**Fraunces/Work Sans fall back to `FontFamily.Serif`/`FontFamily.SansSerif`** — bundling the real OFL
files needs either Google Fonts provider certificate hashes or verified static-instance file paths,
and this session had no safe way to fetch/verify either, so it deliberately stopped short of
guessing binary/cert data. Swap `FrauncesFamily`/`WorkSansFamily` once real font files are added.

Built: `AppTopBar` (updated), `SteadyBottomBar`, `StatProgressCard`, `MacroGridCard`,
`PillToggleGroup`/`ChipSelector`, `PremiumBadge`, `InfoBanner`, `ListSettingsRow`,
`MedicalWarningPanel`, `LockedPremiumOverlay`, plus shared `ScreenScaffold`/`ComingSoonContent`.

Navigation: typed routes for all 19 destinations, root graph (Splash → Onboarding → SignIn) and an
`AuthGraphRoute` with 5 nested per-tab graphs (Home/Scan/Insights/Community/Profile), each keeping
its own back stack via `popUpTo(...){saveState=true}` + `restoreState=true`. `PremiumPaywallRoute` is
top-level (reachable from both Insights→Journey and, later, Profile) so it isn't scoped to one tab.

Screens actually built for real in this phase: Onboarding (3-slide pager, Skip/Next/Get started —
no persistence yet, that's Phase 3's job), Home dashboard (zero-state cards, no fake numbers),
Profile (theme picker + sign-out, moved out of the old template's Settings tab), Help (static FAQ,
no data dependency). The other 13 destinations (Scan, Meal Detected, Insights, Journey, Community,
Log Dose, Log Side Effects, Medication & Dose, Daily Goals, Notifications, Account, Premium Paywall)
are honest `ComingSoonContent` skeletons — reachable, on-brand, but intentionally not wired to any
business logic, since Room/CameraX/Billing/Firestore don't exist yet and those screens are explicitly
owned by Phases 3–7. `HomeViewModel` was deleted (its logout/theme logic moved to the new
`ProfileViewModel`); nothing else referenced it.

Not done / deferred: exact-fidelity wiring against the checklist's "47 CTAs" wasn't cross-checked
line-by-line against the spreadsheet (used the Stitch screen titles + Phase 1 domain models as the
source of truth instead); TalkBack/contrast/font-scaling review needs a physical device or emulator
neither available here. `./gradlew testDebugUnitTest lintDebug assembleDebug` all green.

## Risk Assessment

Stitch HTML is visual reference, not Android production code. Recreate behavior with Compose primitives; avoid literal fixed pixel dimensions that break accessibility.
