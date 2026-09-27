package com.steady.app.core.utils

import android.util.Log
import com.steady.app.data.repository.FirebaseProvider
import timber.log.Timber

class CrashlyticsTree(private val firebaseProvider: FirebaseProvider) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.WARN) return
        firebaseProvider.crashlyticsOrNull()?.apply {
            setCustomKey("log_tag", tag.orEmpty())
            log(message)
            t?.let(::recordException)
        }
    }
}
