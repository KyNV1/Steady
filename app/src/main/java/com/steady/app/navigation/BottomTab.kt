package com.steady.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.steady.app.R

enum class BottomTab(val graphRoute: Any, val startRoute: Any, val icon: ImageVector, val labelRes: Int) {
    HOME(HomeTabGraphRoute, HomeRoute, Icons.Default.Home, R.string.tab_home),
    SCAN(ScanTabGraphRoute, ScanRoute, Icons.Default.CameraAlt, R.string.tab_scan),
    INSIGHTS(InsightsTabGraphRoute, InsightsRoute, Icons.AutoMirrored.Filled.ShowChart, R.string.tab_insights),
    COMMUNITY(CommunityTabGraphRoute, CommunityRoute, Icons.Default.Groups, R.string.tab_community),
    PROFILE(ProfileTabGraphRoute, ProfileRoute, Icons.Default.Person, R.string.tab_profile),
}

@Composable
fun BottomTab.label(): String = stringResource(labelRes)
