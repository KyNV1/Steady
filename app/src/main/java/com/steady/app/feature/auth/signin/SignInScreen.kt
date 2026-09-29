package com.steady.app.feature.auth.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
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
import com.steady.app.ui.theme.SteadyShapes
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onAuthenticated: (PostAuthDestination) -> Unit,
    onBack: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.isAuthenticated) {
        state.nextDestination?.let { destination -> if (state.isAuthenticated) onAuthenticated(destination) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(LocalAppSpacing.current.large),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Surface(
            shape = SteadyShapes.bottomSheetTop,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppSpacing.current.large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(LocalAppSpacing.current.small))
                Text(stringResource(R.string.sign_in_title), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(LocalAppSpacing.current.small))
                Text(
                    stringResource(R.string.sign_in_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(LocalAppSpacing.current.large))

                if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()) {
                    OutlinedButton(
                        onClick = { scope.launch { signInWithGoogle(context, viewModel) } },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.sign_in_with_google))
                    }
                } else {
                    Text(
                        stringResource(R.string.google_sign_in_not_configured),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
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
                    Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }

                Spacer(Modifier.height(LocalAppSpacing.current.small))
                TextButton(onClick = onBack) { Text(stringResource(R.string.sign_in_cancel)) }

                Spacer(Modifier.height(LocalAppSpacing.current.small))
                Text(
                    stringResource(R.string.sign_in_terms_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
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
    } catch (e: GetCredentialCancellationException) {
        viewModel.onGoogleSignInFailed(context.getString(R.string.sign_in_cancelled))
    } catch (e: NoCredentialException) {
        viewModel.onGoogleSignInFailed("No Google account found on this device. Add one in Settings first.")
    } catch (e: GetCredentialException) {
        viewModel.onGoogleSignInFailed(e.message ?: "Google sign-in was cancelled or failed.")
    }
}
