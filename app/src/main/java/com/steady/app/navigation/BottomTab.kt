package com.steady.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.steady.app.R

enum class BottomTab(
    val graphRoute: Any,
    val startRoute: Any,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val labelRes: Int,
) {
    HOME(HomeTabGraphRoute, HomeRoute, Icons.Outlined.Home, Icons.Filled.Home, R.string.tab_home),
    SCAN(ScanTabGraphRoute, ScanRoute, Icons.Outlined.DocumentScanner, Icons.Filled.DocumentScanner, R.string.tab_scan),
    INSIGHTS(InsightsTabGraphRoute, InsightsRoute, Icons.Outlined.BarChart, Icons.Filled.BarChart, R.string.tab_insights),
    COMMUNITY(CommunityTabGraphRoute, CommunityRoute, Icons.Outlined.Group, Icons.Filled.Group, R.string.tab_community),
    PROFILE(ProfileTabGraphRoute, ProfileRoute, Icons.Outlined.Person, Icons.Filled.Person, R.string.tab_profile),
}

@Composable
fun BottomTab.label(): String = stringResource(labelRes)
