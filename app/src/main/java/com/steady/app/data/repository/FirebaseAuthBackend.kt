package com.steady.app.data.repository

import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthBackend @Inject constructor(private val firebaseProvider: FirebaseProvider) : AuthBackend {
    override suspend fun login(googleIdToken: String): BackendAuthSession {
        val auth = requireNotNull(firebaseProvider.authOrNull()) {
            "Firebase is not configured. Add app/google-services.json or use the debug demo account."
        }
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val user = requireNotNull(auth.signInWithCredential(credential).await().user)
        return user.toSession()
    }

    override suspend fun refresh(): BackendAuthSession {
        val auth = requireNotNull(firebaseProvider.authOrNull()) { "Firebase is not configured." }
        val user = requireNotNull(auth.currentUser) { "No active session." }
        return user.toSession(forceRefresh = true)
    }

    override fun logout() {
        firebaseProvider.authOrNull()?.signOut()
    }

    private suspend fun com.google.firebase.auth.FirebaseUser.toSession(
        forceRefresh: Boolean = false,
    ): BackendAuthSession = BackendAuthSession(
        userId = uid,
        email = email.orEmpty(),
        displayName = displayName.orEmpty(),
        accessToken = requireNotNull(getIdToken(forceRefresh).await().token) { "Firebase returned an empty token." },
    )
}
