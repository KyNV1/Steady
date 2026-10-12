package com.steady.app.feature.auth.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.LoadingDialog
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes
import kotlinx.coroutines.launch

private val SheetScrim = Color(0xFF171C18).copy(alpha = 0.45f)
private val DragHandle = Color(0xFFDCD7CE)

/**
 * Sign-in modal sheet. Hosted as a dialog destination so the screen underneath (usually
 * onboarding) stays visible behind the scrim, as in the Stitch design.
 */
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SheetScrim)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onBack),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                // Swallow taps on the sheet so they don't reach the scrim's dismiss handler.
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
            shape = SteadyShapes.bottomSheetTop,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = Dimens.dp_24, end = Dimens.dp_24, top = Dimens.dp_12, bottom = Dimens.dp_28),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .size(width = Dimens.dp_36, height = Dimens.dp_4)
                        .background(DragHandle, CircleShape),
                )
                Spacer(Modifier.height(Dimens.dp_16))
                IconTile(
                    icon = Icons.Outlined.Spa,
                    size = Dimens.dp_40,
                    containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconSize = Dimens.dp_20,
                    border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                )
                Spacer(Modifier.height(Dimens.dp_12))
                Text(
                    stringResource(R.string.sign_in_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = Dimens.sp_24, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Dimens.dp_8))
                Text(
                    stringResource(R.string.sign_in_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 23.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp),
                )
                Spacer(Modifier.height(Dimens.dp_24))

                val notConfigured = stringResource(R.string.google_sign_in_not_configured)
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp_12)) {
                    GoogleSignInButton(onClick = {
                        if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isBlank()) {
                            viewModel.onGoogleSignInFailed(notConfigured)
                        } else {
                            scope.launch { signInWithGoogle(context, viewModel) }
                        }
                    })
                    SheetButton(text = stringResource(R.string.sign_in_cancel), onClick = onBack)
                }
                // Debug-only escape hatch; kept visually minor so the sheet still reads as the design.
                if (BuildConfig.USE_FAKE_AUTH) {
                    TextButton(onClick = viewModel::signInAsDemo) {
                        Text(
                            stringResource(R.string.continue_as_demo),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                state.errorMessage?.let {
                    Spacer(Modifier.height(Dimens.dp_12))
                    Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }

                Spacer(Modifier.height(Dimens.dp_24))
                Text(
                    termsNotice(),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_12, lineHeight = 20.sp),
                    color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 290.dp),
                )
            }
        }
    }
    LoadingDialog(state.isLoading)
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = SteadyShapes.button,
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Icon(
            painterResource(R.drawable.ic_google_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(Dimens.dp_20),
        )
        Spacer(Modifier.width(Dimens.dp_12))
        Text(stringResource(R.string.sign_in_with_google), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium))
    }
}

/** Neutral filled sheet action (Cancel / demo), per the Stitch secondary button. */
@Composable
private fun SheetButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.dp_48),
        shape = SteadyShapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium))
    }
}

/** Underlines the Terms / Privacy Policy phrases inside the localized notice. */
@Composable
private fun termsNotice(): AnnotatedString {
    val notice = stringResource(R.string.sign_in_terms_notice)
    val highlights = listOf(stringResource(R.string.sign_in_terms_link), stringResource(R.string.sign_in_privacy_link))
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium,
        textDecoration = TextDecoration.Underline,
    )
    return buildAnnotatedString {
        append(notice)
        highlights.forEach { phrase ->
            val start = notice.indexOf(phrase)
            if (start >= 0) addStyle(linkStyle, start, start + phrase.length)
        }
    }
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
