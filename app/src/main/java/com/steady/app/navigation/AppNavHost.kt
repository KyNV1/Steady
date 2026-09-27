package com.steady.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.steady.app.feature.auth.signin.SignInScreen
import com.steady.app.feature.home.HomeScreen
import com.steady.app.feature.splash.SplashDestination
import com.steady.app.feature.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = SplashRoute, modifier = modifier) {
        composable<SplashRoute> {
            SplashScreen(onDestination = { destination ->
                val route = if (destination == SplashDestination.HOME) HomeRoute else SignInRoute
                navController.navigate(route) { popUpTo<SplashRoute> { inclusive = true } }
            })
        }
        composable<SignInRoute> {
            SignInScreen(
                onAuthenticated = {
                    navController.navigate(HomeRoute) { popUpTo<SignInRoute> { inclusive = true } }
                },
            )
        }
        composable<HomeRoute>(
            deepLinks = listOf(navDeepLink { uriPattern = DeepLinkHandler.HOME_URI }),
        ) {
            HomeScreen(
                onLoggedOut = {
                    navController.navigate(SignInRoute) { popUpTo<HomeRoute> { inclusive = true } }
                },
            )
        }
    }
}
