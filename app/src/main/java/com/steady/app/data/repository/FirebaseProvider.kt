package com.steady.app.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseProvider @Inject constructor(@ApplicationContext private val context: Context) {
    fun initialize(): Boolean = FirebaseApp.getApps(context).isNotEmpty() || FirebaseApp.initializeApp(context) != null

    fun authOrNull(): FirebaseAuth? = if (initialize()) FirebaseAuth.getInstance() else null

    fun crashlyticsOrNull(): FirebaseCrashlytics? = if (initialize()) FirebaseCrashlytics.getInstance() else null
}
