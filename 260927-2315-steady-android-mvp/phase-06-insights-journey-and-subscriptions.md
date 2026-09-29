---
phase: 6
title: "Insights, Journey and subscriptions"
status: pending
priority: P1
dependencies: [4, 5]
effort: "7-9 days"
---

# Phase 6: Insights, Journey and Subscriptions

## Overview

Turn accumulated data into weekly insights and monetize only premium value through server-verified Google Play entitlements.

## Related Code Files

- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/insights/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/journey/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/subscription/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/repository/BillingRepositoryImpl.kt`
- Create: `D:/chplay/Steady/functions/src/verify-purchase.ts`, `play-rtdn.ts`
- Modify: `D:/chplay/Steady/storage.rules`, `D:/chplay/Steady/firestore.rules`

## Implementation Steps

1. Query seven-day aggregates for protein/fiber/water/calories and streak/average cards.
2. Build locked Journey preview and route both it and Profile to the same paywall.
3. Configure weekly/yearly products and trial in Play Console; fetch live `ProductDetails` rather than hardcoded prices.
4. Implement one lifecycle-aware BillingClient, pending/cancel/error states and restore/query purchases on resume.
5. Send purchase token with obfuscated account ID to backend; verify via Play Developer API, acknowledge securely and write authoritative entitlement.
6. Consume RTDN idempotently for renewal, grace, hold, cancellation and expiration.
7. For entitled users, save weight and UID-private progress photos; signed-in owner access only.

## Success Criteria

- [ ] Core meal/dose tracking remains usable without Premium.
- [ ] Pending or locally forged purchases never unlock Journey.
- [ ] Prices/offers come from Play and purchase restoration works after reinstall.
- [ ] Subscription state follows renewal, grace, hold and expiration events.
- [ ] Progress photos cannot be read or listed by another UID.

## Risk Assessment

Billing has financial/security blast radius. Use license testers, backend verification and RTDN; do not rely on callback success or a DataStore boolean.
