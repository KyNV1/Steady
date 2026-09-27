package com.steady.app.feature.auth.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.steady.app.BuildConfig
import com.steady.app.R
import com.steady.app.ui.components.AppButton
import com.steady.app.ui.components.AppButtonStyle
import com.steady.app.ui.components.LoadingDialog
import com.steady.app.ui.theme.LocalAppSpacing
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onAuthenticated: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.isAuthenticated) { if (state.isAuthenticated) onAuthenticated() }

    Column(
        modifier = Modifier.fillMaxSize().padding(LocalAppSpacing.current.large),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.sign_in_title), style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(R.string.sign_in_subtitle), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(LocalAppSpacing.current.large))

        if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()) {
            AppButton(
                text = stringResource(R.string.sign_in_with_google),
                onClick = {
                    scope.launch {
                        signInWithGoogle(context, viewModel)
                    }
                },
            )
        } else {
            Text(stringResource(R.string.google_sign_in_not_configured), style = MaterialTheme.typography.bodySmall)
        }

        if (BuildConfig.USE_FAKE_AUTH) {
            Spacer(Modifier.height(LocalAppSpacing.current.small))
            AppButton(
                text = stringResource(R.string.continue_as_demo),
                onClick = viewModel::signInAsDemo,
                style = AppButtonStyle.OUTLINED,
            )
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(LocalAppSpacing.current.small))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
    LoadingDialog(state.isLoading)
}

private suspend fun signInWithGoogle(context: android.content.Context, viewModel: SignInViewModel) {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()
    val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

    try {
        val result = CredentialManager.create(context).getCredential(context, request)
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
            viewModel.signInWithGoogle(idToken)
        }
    } catch (e: NoCredentialException) {
        viewModel.onGoogleSignInFailed("No Google account found on this device. Add one in Settings first.")
    } catch (e: GetCredentialException) {
        viewModel.onGoogleSignInFailed(e.message ?: "Google sign-in was cancelled or failed.")
    }
}
