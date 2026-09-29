package com.steady.app.navigation

import kotlinx.serialization.Serializable

@Serializable data object SplashRoute
@Serializable data object OnboardingRoute
@Serializable data object SignInRoute
@Serializable data object MedicationSetupRoute
@Serializable data object GoalsSetupRoute

@Serializable data object AuthGraphRoute

@Serializable data object HomeTabGraphRoute
@Serializable data object HomeRoute
@Serializable data object LogDoseRoute
@Serializable data object LogSideEffectRoute
@Serializable data object MedicationDoseRoute
@Serializable data object DailyGoalsRoute

@Serializable data object ScanTabGraphRoute
@Serializable data object ScanRoute
@Serializable data object MealDetectedRoute

@Serializable data object InsightsTabGraphRoute
@Serializable data object InsightsRoute
@Serializable data object JourneyRoute

@Serializable data object CommunityTabGraphRoute
@Serializable data object CommunityRoute

@Serializable data object ProfileTabGraphRoute
@Serializable data object ProfileRoute
@Serializable data object NotificationsRoute
@Serializable data object AccountRoute
@Serializable data object HelpRoute

@Serializable data object PremiumPaywallRoute
