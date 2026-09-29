---
phase: 7
title: "Notifications, account privacy and Community"
status: pending
priority: P1
dependencies: [3, 4, 6]
effort: "6-10 days (Community optional for MVP)"
---

# Phase 7: Notifications, Account Privacy and Community

## Overview

Finish account/privacy and reminder flows required for safe release; implement Community only after the MVP gate if it remains in launch scope.

## Related Code Files

- Modify: `D:/chplay/Steady/app/src/main/java/com/steady/app/core/notifications/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/notifications/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/account/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/feature/community/*`
- Create: `D:/chplay/Steady/app/src/main/java/com/steady/app/data/repository/CommunityRepositoryImpl.kt`
- Create: `D:/chplay/Steady/functions/src/delete-account.ts`, `moderate-community.ts`
- Modify: `D:/chplay/Steady/firestore.rules`, `D:/chplay/Steady/storage.rules`

## Implementation Steps

1. Build notification inbox; schedule local dose reminders with WorkManager and exact-alarm-free fallback behavior.
2. Add FCM token registration/rotation only after consent and authenticated profile availability.
3. Build Account with Google-only fields, logout and reauthentication-aware delete flow.
4. Implement backend deletion orchestration across Firestore/Storage/Auth with retry/audit state and user-visible completion.
5. Keep Community read-only/hidden until rules and moderation are ready.
6. Optional P1: implement paginated feed, composer, detail/comments, atomic like/comment counts, per-user limits, report and moderation queue.
7. Test Firestore/Storage rules in emulator for owner, other user, unauthenticated and malformed writes.

## Success Criteria

- [ ] Dose reminder survives reboot/rescheduling and respects notification permission.
- [ ] Logout clears local user-scoped state; delete removes cloud/private media or reports retry state.
- [ ] Community cannot launch with permissive rules or without report/moderation controls.
- [ ] Like/comment retries are idempotent and counts cannot be forged by clients.

## Risk Assessment

Community introduces medical misinformation and moderation obligations. Default recommendation: defer writable Community from MVP; retain the designed tab as a disabled/coming-soon state only if product requires visual parity.
