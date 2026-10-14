package com.steady.app.data.repository

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.steady.app.BuildConfig
import com.steady.app.domain.repository.AccountDeletionError
import com.steady.app.domain.repository.AccountDeletionException
import com.steady.app.domain.repository.AccountDeletionState
import com.steady.app.domain.repository.AccountRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import timber.log.Timber

private const val DELETE_ACCOUNT_FUNCTION = "deleteAccount"

/**
 * Asks the protected `deleteAccount` callable to erase Auth, Firestore and Storage data. The server
 * owns the deletion job; the app only reports its state and never deletes cloud data itself.
 */
@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val firebaseProvider: FirebaseProvider,
) : AccountRepository {
    override suspend fun requestAccountDeletion(): Result<AccountDeletionState> {
        if (!BuildConfig.FIREBASE_CONFIGURED || !firebaseProvider.initialize()) {
            return Result.failure(AccountDeletionException(AccountDeletionError.UNAVAILABLE))
        }
        return try {
            val response = FirebaseFunctions.getInstance().getHttpsCallable(DELETE_ACCOUNT_FUNCTION).call().await()
            Result.success(parseDeletionState(response.getData()))
        } catch (e: FirebaseFunctionsException) {
            Timber.w("deleteAccount failed: %s", e.code)
            Result.failure(AccountDeletionException(e.code.toDeletionError()))
        } catch (e: IOException) {
            Result.failure(AccountDeletionException(AccountDeletionError.NETWORK))
        }
    }
}

/** Unknown or missing states fall back to RETRY_REQUIRED so the app never assumes data is gone. */
internal fun parseDeletionState(data: Any?): AccountDeletionState {
    val state = (data as? Map<*, *>)?.get("state") as? String
    return AccountDeletionState.entries.firstOrNull { it.name == state } ?: AccountDeletionState.RETRY_REQUIRED
}

private fun FirebaseFunctionsException.Code.toDeletionError(): AccountDeletionError = when (this) {
    FirebaseFunctionsException.Code.UNAUTHENTICATED,
    FirebaseFunctionsException.Code.PERMISSION_DENIED -> AccountDeletionError.REAUTHENTICATION_REQUIRED
    FirebaseFunctionsException.Code.UNAVAILABLE,
    FirebaseFunctionsException.Code.DEADLINE_EXCEEDED -> AccountDeletionError.NETWORK
    else -> AccountDeletionError.UNAVAILABLE
}
