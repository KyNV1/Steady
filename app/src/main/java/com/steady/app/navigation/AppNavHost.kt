package com.steady.app.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.steady.app.feature.account.AccountScreen
import com.steady.app.feature.auth.signin.PostAuthDestination
import com.steady.app.feature.auth.signin.SignInScreen
import com.steady.app.feature.community.CommunityScreen
import com.steady.app.feature.dose.LogDoseScreen
import com.steady.app.feature.goals.DailyGoalsScreen
import com.steady.app.feature.goals.GoalsSetupScreen
import com.steady.app.feature.help.HelpScreen
import com.steady.app.feature.home.HomeScreen
import com.steady.app.feature.insights.InsightsScreen
import com.steady.app.feature.journey.JourneyScreen
import com.steady.app.feature.mealresult.MealDetectedScreen
import com.steady.app.feature.medication.MedicationDoseScreen
import com.steady.app.feature.medication.MedicationSetupScreen
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

    // Each tab keeps its own back stack: save the outgoing tab's state, restore the incoming one's.
    val switchTab: (BottomTab) -> Unit = { tab ->
        navController.navigate(tab.startRoute) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            // The scan flow (camera → result) is full-screen; it exits via its own back button.
            if (currentTab != null && currentTab != BottomTab.SCAN) {
                SteadyBottomBar(selected = currentTab, onSelect = switchTab)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            modifier = modifier
                .padding(bottom = padding.calculateBottomPadding())
                .consumeWindowInsets(WindowInsets(bottom = padding.calculateBottomPadding())),
        ) {
            composable<SplashRoute> {
                SplashScreen(onDestination = { destination ->
                    val route = when (destination) {
                        SplashDestination.ONBOARDING -> OnboardingRoute
                        SplashDestination.SIGN_IN -> SignInRoute
                        SplashDestination.PROFILE_SETUP -> MedicationSetupRoute
                        SplashDestination.HOME -> AuthGraphRoute
                    }
                    navController.navigate(route) { popUpTo<SplashRoute> { inclusive = true } }
                })
            }
            composable<OnboardingRoute> {
                val goToSignIn = { navController.navigate(SignInRoute) }
                OnboardingScreen(onFinished = goToSignIn, onSignIn = goToSignIn)
            }
            dialog<SignInRoute>(
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
            ) {
                // SignInScreen draws its own scrim; drop the platform dim so it isn't doubled.
                (LocalView.current.parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
                SignInScreen(
                    onAuthenticated = { destination ->
                        val route = if (destination == PostAuthDestination.HOME) AuthGraphRoute else MedicationSetupRoute
                        navController.navigate(route) { popUpTo(navController.graph.id) { inclusive = true } }
                    },
                    onBack = navController::popBackStack,
                )
            }
            composable<MedicationSetupRoute> {
                MedicationSetupScreen(onContinue = { navController.navigate(GoalsSetupRoute) })
            }
            composable<GoalsSetupRoute> {
                GoalsSetupScreen(onFinished = {
                    navController.navigate(AuthGraphRoute) { popUpTo(navController.graph.id) { inclusive = true } }
                })
            }

            navigation<AuthGraphRoute>(startDestination = HomeTabGraphRoute) {
                navigation<HomeTabGraphRoute>(
                    startDestination = HomeRoute,
                    deepLinks = listOf(navDeepLink { uriPattern = DeepLinkHandler.HOME_URI }),
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            onScanMeal = { switchTab(BottomTab.SCAN) },
                            onLogDose = { navController.navigate(LogDoseRoute) },
                            onSetUpMedication = { navController.navigate(MedicationDoseRoute) },
                            onLogSideEffect = { navController.navigate(LogSideEffectRoute) },
                            onOpenInsights = { switchTab(BottomTab.INSIGHTS) },
                            onOpenNotifications = { navController.navigate(NotificationsRoute) },
                        )
                    }
                }

                navigation<ScanTabGraphRoute>(startDestination = ScanRoute) {
                    composable<ScanRoute> {
                        ScanScreen(
                            onBack = { switchTab(BottomTab.HOME) },
                            onMealCaptured = { uri -> navController.navigate(MealDetectedRoute(uri)) },
                        )
                    }
                    composable<MealDetectedRoute> {
                        MealDetectedScreen(
                            onBack = navController::popBackStack,
                            onRetake = navController::popBackStack,
                            onLogged = {
                                // Leave the scan tab reset to the camera, then show the updated totals on Home.
                                navController.popBackStack<ScanRoute>(inclusive = false)
                                switchTab(BottomTab.HOME)
                            },
                        )
                    }
                }

                navigation<InsightsTabGraphRoute>(startDestination = InsightsRoute) {
                    composable<InsightsRoute> {
                        InsightsScreen(
                            onOpenJourney = { navController.navigate(JourneyRoute) },
                            onOpenNotifications = { navController.navigate(NotificationsRoute) },
                        )
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
                            onOpenSubscription = { navController.navigate(PremiumPaywallRoute) },
                            onLogDose = { navController.navigate(LogDoseRoute) },
                        )
                    }
                    composable<MedicationDoseRoute> { MedicationDoseScreen(onBack = navController::popBackStack) }
                    composable<DailyGoalsRoute> { DailyGoalsScreen(onBack = navController::popBackStack) }
                    composable<AccountRoute> {
                        AccountScreen(
                            onBack = navController::popBackStack,
                            onSignedOut = {
                                navController.navigate(SignInRoute) {
                                    popUpTo<AuthGraphRoute> { inclusive = true }
                                }
                            },
                        )
                    }
                    composable<HelpRoute> { HelpScreen(onBack = navController::popBackStack) }
                }
            }

            composable<PremiumPaywallRoute> { PremiumPaywallScreen(onBack = navController::popBackStack) }
            // Top-level (not inside a tab graph): full-screen flows shown without the tab bar.
            composable<NotificationsRoute> { NotificationsScreen(onBack = navController::popBackStack) }
            composable<LogSideEffectRoute> { LogSideEffectScreen(onBack = navController::popBackStack) }
            composable<LogDoseRoute> {
                LogDoseScreen(
                    onBack = navController::popBackStack,
                    onSetUpMedication = {
                        navController.navigate(MedicationDoseRoute) { popUpTo<LogDoseRoute> { inclusive = true } }
                    },
                )
            }
        }
    }
}
