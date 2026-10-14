package com.steady.app.feature.notifications

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.domain.model.AppNotification
import com.steady.app.domain.model.NotificationType
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors

private val GroupCardShape = RoundedCornerShape(20.dp)

@Composable
fun NotificationsScreen(onBack: () -> Unit, viewModel: NotificationsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NotificationsContent(
        state = state,
        onBack = onBack,
        onMarkAllRead = viewModel::markAllRead,
        onOpen = viewModel::markRead,
        onLoadSamples = viewModel::loadSamples,
    )
}

/** Stateless inbox layout, so it renders in previews without Hilt. */
@Composable
fun NotificationsContent(
    state: NotificationsUiState,
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    onOpen: (AppNotification) -> Unit,
    onLoadSamples: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        NotificationsTopBar(hasUnread = state.hasUnread, onBack = onBack, onMarkAllRead = onMarkAllRead)
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        when {
            state.isLoading -> Unit
            state.isEmpty -> EmptyInbox(canLoadSamples = state.canLoadSamples, onLoadSamples = onLoadSamples)
            else -> LazyColumn(contentPadding = PaddingValues(bottom = Dimens.dp_32)) {
                state.sections.forEach { section ->
                    item(key = section.group) {
                        SectionLabel(section.group)
                        NotificationGroupCard(section.items, nowMillis = state.nowMillis, onOpen = onOpen)
                    }
                }
                item { CaughtUpPill(Modifier.padding(top = Dimens.dp_32)) }
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(hasUnread: Boolean, onBack: () -> Unit, onMarkAllRead: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp_24, vertical = Dimens.dp_12),
    ) {
        IconTile(
            icon = Icons.Default.ChevronLeft,
            size = 34.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.onSurface,
            iconSize = Dimens.dp_20,
            border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
            contentDescription = stringResource(R.string.common_back),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clip(CircleShape)
                .clickable(onClick = onBack),
        )
        Text(
            stringResource(R.string.notifications_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center),
        )
        TextButton(
            onClick = onMarkAllRead,
            enabled = hasUnread,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Text(
                stringResource(if (hasUnread) R.string.notifications_mark_all_read else R.string.notifications_all_caught_up),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = Dimens.sp_13,
                    fontWeight = if (hasUnread) FontWeight.Medium else FontWeight.SemiBold,
                ),
                color = if (hasUnread) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SectionLabel(group: NotificationGroup) {
    val label = when (group) {
        NotificationGroup.TODAY -> R.string.notifications_section_today
        NotificationGroup.EARLIER_THIS_WEEK -> R.string.notifications_section_earlier
        NotificationGroup.OLDER -> R.string.notifications_section_older
    }
    Text(
        stringResource(label).uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = Dimens.dp_24, end = Dimens.dp_24, top = Dimens.dp_22, bottom = Dimens.dp_8),
    )
}

@Composable
private fun NotificationGroupCard(items: List<AppNotification>, nowMillis: Long, onOpen: (AppNotification) -> Unit) {
    SteadyCard(
        shape = GroupCardShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp_20),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                NotificationRow(item, nowMillis = nowMillis, onClick = { onOpen(item) })
            }
        }
    }
}

private data class TypeStyle(val icon: ImageVector, val tint: Color, val tile: Color)

@Composable
private fun NotificationType.style(): TypeStyle {
    val extended = LocalSteadyExtendedColors.current
    val green = extended.surfaceContainerGreen
    val orange = extended.surfaceContainerOrange
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    return when (this) {
        NotificationType.DOSE_REMINDER -> TypeStyle(Icons.Filled.Vaccines, primary, green)
        NotificationType.MEAL_REMINDER -> TypeStyle(Icons.Outlined.Restaurant, secondary, orange)
        NotificationType.COMMUNITY_LIKE -> TypeStyle(Icons.Filled.Favorite, primary, green)
        NotificationType.COMMUNITY_REPLY ->
            TypeStyle(Icons.Outlined.ChatBubbleOutline, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surfaceContainer)
        NotificationType.STREAK -> TypeStyle(Icons.Filled.LocalFireDepartment, secondary, orange)
        NotificationType.WEEKLY_INSIGHT -> TypeStyle(Icons.AutoMirrored.Outlined.TrendingUp, primary, green)
        NotificationType.SYSTEM -> TypeStyle(Icons.Outlined.Spa, primary, green)
    }
}

@Composable
private fun NotificationRow(item: AppNotification, nowMillis: Long, onClick: () -> Unit) {
    val style = item.type.style()
    val muted = LocalSteadyExtendedColors.current.onSurfaceMuted
    val isQuote = item.type == NotificationType.COMMUNITY_LIKE || item.type == NotificationType.COMMUNITY_REPLY
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Dimens.dp_14),
        verticalAlignment = Alignment.Top,
    ) {
        IconTile(
            icon = style.icon,
            size = Dimens.dp_38,
            shape = RoundedCornerShape(Dimens.dp_12),
            containerColor = style.tile,
            iconTint = style.tint,
            iconSize = Dimens.dp_20,
            modifier = Modifier.padding(top = Dimens.dp_2),
        )
        Column(
            Modifier
                .weight(1f)
                .padding(start = Dimens.dp_14, end = Dimens.dp_8),
        ) {
            Text(
                item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                item.body,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    fontStyle = if (item.type == NotificationType.COMMUNITY_LIKE) FontStyle.Italic else FontStyle.Normal,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isQuote) 1 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = Dimens.dp_2),
            )
            Text(
                DateUtils.getRelativeTimeSpanString(
                    item.createdAtEpochMillis,
                    nowMillis,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE,
                ).toString(),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_11),
                color = muted,
                modifier = Modifier.padding(top = Dimens.dp_4),
            )
        }
        if (item.isUnread) {
            val unread = stringResource(R.string.notifications_unread_cd)
            Box(
                Modifier
                    .padding(top = Dimens.dp_8)
                    .size(Dimens.dp_8)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    .semantics { contentDescription = unread },
            )
        }
    }
}

@Composable
private fun CaughtUpPill(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(
            shape = CircleShape,
            color = LocalSteadyExtendedColors.current.surfaceContainerGreen.copy(alpha = 0.6f),
            border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Dimens.dp_12, vertical = Dimens.dp_6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(Modifier.width(Dimens.dp_6))
                Text(
                    stringResource(R.string.notifications_footer),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun EmptyInbox(canLoadSamples: Boolean, onLoadSamples: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.dp_24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CaughtUpPill()
        if (canLoadSamples) {
            Spacer(Modifier.height(Dimens.dp_12))
            TextButton(onClick = onLoadSamples) {
                Text(
                    stringResource(R.string.notifications_load_samples),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Notifications", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun NotificationsScreenPreview() {
    val now = 1_790_053_200_000L
    AppTheme(darkTheme = false) {
        NotificationsContent(
            state = NotificationsUiState(
                isLoading = false,
                sections = groupNotifications(debugSampleNotifications("preview", now), now),
                nowMillis = now,
            ),
            onBack = {},
            onMarkAllRead = {},
            onOpen = {},
            onLoadSamples = {},
        )
    }
}

@Preview(name = "Notifications - empty", showBackground = true, widthDp = 390, heightDp = 500)
@Composable
private fun NotificationsEmptyPreview() {
    AppTheme(darkTheme = false) {
        NotificationsContent(
            state = NotificationsUiState(isLoading = false, canLoadSamples = true),
            onBack = {},
            onMarkAllRead = {},
            onOpen = {},
            onLoadSamples = {},
        )
    }
}
