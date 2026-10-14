package com.steady.app.feature.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.AppTopBar
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.LoadingDialog
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors

@Composable
fun AccountScreen(onBack: () -> Unit, onSignedOut: () -> Unit, viewModel: AccountViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.signedOut) { if (state.signedOut) onSignedOut() }
    val message = state.message?.let { stringResource(it.textRes()) }
    LaunchedEffect(message) {
        if (message != null) {
            snackbarHost.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    Box(Modifier.fillMaxSize()) {
        AccountContent(
            state = state,
            onBack = onBack,
            onChangePhoto = viewModel::onChangePhoto,
            onSignOut = viewModel::signOut,
            onDeleteAccount = { confirmDelete = true },
        )
        SnackbarHost(snackbarHost, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.account_delete_confirm_title)) },
            text = { Text(stringResource(R.string.account_delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.deleteAccount()
                }) {
                    Text(stringResource(R.string.account_delete_confirm_action), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.sign_in_cancel)) }
            },
        )
    }
    LoadingDialog(state.isBusy)
}

/** Stateless Account layout, so it renders in previews without Hilt. */
@Composable
fun AccountContent(
    state: AccountUiState,
    onBack: () -> Unit,
    onChangePhoto: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        AppTopBar(title = stringResource(R.string.account_title), onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = Dimens.dp_24, end = Dimens.dp_24, bottom = Dimens.dp_40),
        ) {
            AvatarSection(state.displayName, onChangePhoto)

            SectionLabel(stringResource(R.string.account_personal_info))
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    InfoRow(stringResource(R.string.account_name), state.displayName.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    InfoRow(stringResource(R.string.account_email), state.email.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    InfoRow(stringResource(R.string.account_sign_in_method), stringResource(R.string.account_sign_in_google))
                }
            }
            Text(
                stringResource(R.string.account_google_managed),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Dimens.dp_4, top = 8.dp, bottom = 24.dp),
            )

            SectionLabel(stringResource(R.string.account_section_account))
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ActionRow(
                        Icons.AutoMirrored.Outlined.Logout,
                        stringResource(R.string.logout_action),
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        LocalSteadyExtendedColors.current.onSurfaceMuted,
                        onSignOut,
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    val error = MaterialTheme.colorScheme.error
                    ActionRow(Icons.Outlined.Delete, stringResource(R.string.account_delete), error, error, error.copy(alpha = 0.6f), onDeleteAccount)
                }
            }

            Text(
                stringResource(R.string.account_privacy_note),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = LocalSteadyExtendedColors.current.onSurfaceMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 32.dp),
            )
        }
    }
}

@Composable
private fun AvatarSection(displayName: String, onChangePhoto: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    displayName.firstOrNull()?.uppercase() ?: "S",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            IconTile(
                icon = Icons.Outlined.PhotoCamera,
                size = 28.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = 15.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentDescription = stringResource(R.string.account_change_photo),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .clickable(onClick = onChangePhoto),
            )
        }
        TextButton(onClick = onChangePhoto, modifier = Modifier.padding(top = 4.dp)) {
            Text(
                stringResource(R.string.account_change_photo),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )
}

/** Read-only field: name and email come from the Google account and can't be edited here. */
@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun ActionRow(icon: ImageVector, label: String, textColor: Color, iconTint: Color, chevronTint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textColor,
            modifier = Modifier.weight(1f),
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint, modifier = Modifier.size(20.dp))
    }
}

private fun AccountMessage.textRes(): Int = when (this) {
    AccountMessage.PHOTO_COMING_SOON -> R.string.account_photo_coming_soon
    AccountMessage.DELETE_UNAVAILABLE -> R.string.account_delete_unavailable
    AccountMessage.DELETE_REAUTH -> R.string.account_delete_reauth
    AccountMessage.DELETE_NETWORK -> R.string.account_delete_network
    AccountMessage.DELETE_RETRY -> R.string.account_delete_retry
}

@Preview(name = "Account", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AccountScreenPreview() {
    AppTheme(darkTheme = false) {
        AccountContent(
            state = AccountUiState(displayName = "Alex Nguyen", email = "alex.nguyen@email.com"),
            onBack = {},
            onChangePhoto = {},
            onSignOut = {},
            onDeleteAccount = {},
        )
    }
}
