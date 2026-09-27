package com.steady.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.steady.app.feature.account.AccountScreen
import com.steady.app.feature.auth.signin.SignInScreen
import com.steady.app.feature.community.CommunityScreen
import com.steady.app.feature.dose.LogDoseScreen
import com.steady.app.feature.goals.DailyGoalsScreen
import com.steady.app.feature.help.HelpScreen
import com.steady.app.feature.home.HomeScreen
import com.steady.app.feature.insights.InsightsScreen
import com.steady.app.feature.journey.JourneyScreen
import com.steady.app.feature.mealresult.MealDetectedScreen
import com.steady.app.feature.medication.MedicationDoseScreen
import com.steady.app.feature.notifications.NotificationsScreen
import com.steady.app.feature.onboarding.OnboardingScreen
import com.steady.app.feature.profile.ProfileScreen
import com.steady.app.feature.scan.ScanScreen
import com.steady.app.feature.sideeffects.LogSideEffectScreen
import com.steady.app.feature.splash.SplashDestination
import com.steady.app.feature.splash.SplashScreen
import com.steady.app.feature.subscription.PremiumPaywallScreen
import com.steady.app.ui.components.SteadyBottomBar

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = backStackEntry?.destination?.let { destination ->
        BottomTab.entries.firstOrNull { tab -> destination.hierarchy.any { it.hasRoute(tab.graphRoute::class) } }
    }

    Scaffold(
        bottomBar = {
            if (currentTab != null) {
                SteadyBottomBar(
                    selected = currentTab,
                    onSelect = { tab ->
                        navController.navigate(tab.startRoute) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            modifier = modifier.padding(bottom = padding.calculateBottomPadding()),
        ) {
            composable<SplashRoute> {
                SplashScreen(onDestination = { destination ->
                    val route = if (destination == SplashDestination.HOME) AuthGraphRoute else OnboardingRoute
                    navController.navigate(route) { popUpTo<SplashRoute> { inclusive = true } }
                })
            }
            composable<OnboardingRoute> {
                OnboardingScreen(onFinished = {
                    navController.navigate(SignInRoute) { popUpTo<OnboardingRoute> { inclusive = true } }
                })
            }
            composable<SignInRoute> {
                SignInScreen(onAuthenticated = {
                    navController.navigate(AuthGraphRoute) { popUpTo<SignInRoute> { inclusive = true } }
                })
            }

            navigation<AuthGraphRoute>(startDestination = HomeTabGraphRoute) {
                navigation<HomeTabGraphRoute>(
                    startDestination = HomeRoute,
                    deepLinks = listOf(navDeepLink { uriPattern = DeepLinkHandler.HOME_URI }),
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            onLogDose = { navController.navigate(LogDoseRoute) },
                            onLogSideEffect = { navController.navigate(LogSideEffectRoute) },
                            onScanMeal = {
                                navController.navigate(ScanTabGraphRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                    composable<LogDoseRoute> { LogDoseScreen(onBack = navController::popBackStack) }
                    composable<LogSideEffectRoute> { LogSideEffectScreen(onBack = navController::popBackStack) }
                }

                navigation<ScanTabGraphRoute>(startDestination = ScanRoute) {
                    composable<ScanRoute> { ScanScreen() }
                    composable<MealDetectedRoute> { MealDetectedScreen(onBack = navController::popBackStack) }
                }

                navigation<InsightsTabGraphRoute>(startDestination = InsightsRoute) {
                    composable<InsightsRoute> {
                        InsightsScreen(onOpenJourney = { navController.navigate(JourneyRoute) })
                    }
                    composable<JourneyRoute> {
                        JourneyScreen(
                            onBack = navController::popBackStack,
                            onUnlock = { navController.navigate(PremiumPaywallRoute) },
                        )
                    }
                }

                navigation<CommunityTabGraphRoute>(startDestination = CommunityRoute) {
                    composable<CommunityRoute> { CommunityScreen() }
                }

                navigation<ProfileTabGraphRoute>(startDestination = ProfileRoute) {
                    composable<ProfileRoute> {
                        ProfileScreen(
                            onLoggedOut = {
                                navController.navigate(SignInRoute) {
                                    popUpTo<AuthGraphRoute> { inclusive = true }
                                }
                            },
                            onOpenDailyGoals = { navController.navigate(DailyGoalsRoute) },
                            onOpenMedicationDose = { navController.navigate(MedicationDoseRoute) },
                            onOpenNotifications = { navController.navigate(NotificationsRoute) },
                            onOpenAccount = { navController.navigate(AccountRoute) },
                            onOpenHelp = { navController.navigate(HelpRoute) },
                        )
                    }
                    composable<MedicationDoseRoute> { MedicationDoseScreen(onBack = navController::popBackStack) }
                    composable<DailyGoalsRoute> { DailyGoalsScreen(onBack = navController::popBackStack) }
                    composable<NotificationsRoute> { NotificationsScreen(onBack = navController::popBackStack) }
                    composable<AccountRoute> { AccountScreen(onBack = navController::popBackStack) }
                    composable<HelpRoute> { HelpScreen(onBack = navController::popBackStack) }
                }
            }

            composable<PremiumPaywallRoute> { PremiumPaywallScreen(onBack = navController::popBackStack) }
        }
    }
}
