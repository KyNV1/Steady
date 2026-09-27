package com.steady.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.steady.app.core.storage.AppDataStore
import com.steady.app.core.storage.ThemeMode
import com.steady.app.navigation.AppNavHost
import com.steady.app.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var appDataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppRoot(appDataStore) }
    }
}

@Composable
private fun AppRoot(appDataStore: AppDataStore) {
    val themeMode by appDataStore.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    AppTheme(darkTheme = darkTheme) {
        AppNavHost(rememberNavController())
    }
}
