package com.steady.app.feature.community

import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steady.app.R
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.AppTheme
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(viewModel: CommunityViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CommunityContent(state = state, onFilter = viewModel::setFilter, onLoadSamples = viewModel::loadSamples)
}

/** Stateless feed layout, so it renders in previews without Hilt. */
@Composable
fun CommunityContent(state: CommunityUiState, onFilter: (PostCategory?) -> Unit, onLoadSamples: () -> Unit) {
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val postingSoon = stringResource(R.string.community_posting_soon)

    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = Dimens.dp_24),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Dimens.dp_24, end = Dimens.dp_24, top = Dimens.dp_12, bottom = Dimens.dp_16),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.community_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = Dimens.sp_28, lineHeight = 34.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                IconTile(
                    icon = Icons.Default.Add,
                    size = Dimens.dp_36,
                    shape = RoundedCornerShape(Dimens.dp_12),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    iconTint = Color.White,
                    iconSize = Dimens.dp_20,
                    contentDescription = stringResource(R.string.community_create_post),
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.dp_12))
                        .clickable { scope.launch { snackbarHost.showSnackbar(postingSoon) } },
                )
            }

            FilterRow(selected = state.filter, onFilter = onFilter)

            Column(
                modifier = Modifier.padding(start = Dimens.dp_24, end = Dimens.dp_24, top = Dimens.dp_16),
                verticalArrangement = Arrangement.spacedBy(Dimens.dp_12),
            ) {
                val posts = state.visiblePosts
                if (posts.isEmpty()) {
                    EmptyFeed(showSampleButton = state.canLoadSamples && state.posts.isEmpty(), onLoadSamples = onLoadSamples)
                } else {
                    posts.forEach { PostCard(it, nowMillis = state.nowMillis) }
                }
                ModerationNote()
            }
        }
        SnackbarHost(snackbarHost, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun FilterRow(selected: PostCategory?, onFilter: (PostCategory?) -> Unit) {
    val options: List<Pair<PostCategory?, Int>> = listOf(
        null to R.string.community_filter_all,
        PostCategory.TIPS_RECIPES to R.string.community_filter_tips,
        PostCategory.MILESTONES to R.string.community_filter_milestones,
        PostCategory.SIDE_EFFECTS to R.string.community_filter_side_effects,
    )
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Dimens.dp_24),
        horizontalArrangement = Arrangement.spacedBy(Dimens.dp_8),
    ) {
        options.forEach { (category, label) ->
            val isSelected = category == selected
            Surface(
                shape = SteadyShapes.chip,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
                border = if (isSelected) null else BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                modifier = Modifier.selectable(selected = isSelected, role = Role.Tab, onClick = { onFilter(category) }),
            ) {
                Text(
                    stringResource(label),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Dimens.dp_14, vertical = Dimens.dp_6),
                )
            }
        }
    }
}

@Composable
private fun PostCard(post: CommunityPost, nowMillis: Long) {
    val extended = LocalSteadyExtendedColors.current
    val warmAvatar = post.authorName.first().code % 2 == 1
    val milestone = post.category == PostCategory.MILESTONES
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(Dimens.dp_16), verticalArrangement = Arrangement.spacedBy(Dimens.dp_14)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(Dimens.dp_38)
                        .background(if (warmAvatar) extended.surfaceContainerOrange else extended.surfaceContainerGreen, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        post.authorName.first().uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = Dimens.sp_16, fontWeight = FontWeight.SemiBold),
                        color = if (warmAvatar) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = Dimens.dp_12),
                ) {
                    Text(post.authorName, style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp), color = MaterialTheme.colorScheme.onSurface)
                    Surface(
                        shape = SteadyShapes.chip,
                        color = extended.surfaceContainerGreen,
                        modifier = Modifier.padding(top = Dimens.dp_2),
                    ) {
                        Text(
                            post.medicationBadge,
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = Dimens.dp_8, vertical = Dimens.dp_2),
                        )
                    }
                }
                Text(
                    DateUtils.getRelativeTimeSpanString(
                        post.createdAtEpochMillis,
                        nowMillis,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE,
                    ).toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                post.body,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            HorizontalDivider(color = extended.outlineSubtle)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Engagement(
                    Icons.Outlined.FavoriteBorder,
                    post.likeCount,
                    pluralStringResource(R.plurals.community_likes_cd, post.likeCount, post.likeCount),
                )
                Spacer(Modifier.width(Dimens.dp_20))
                Engagement(
                    Icons.AutoMirrored.Outlined.Chat,
                    post.commentCount,
                    pluralStringResource(R.plurals.community_comments_cd, post.commentCount, post.commentCount),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    post.hashtag,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
                    color = if (milestone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.dp_6))
                        .background(if (milestone) extended.surfaceContainerOrange else extended.surfaceContainerGreen)
                        .padding(horizontal = Dimens.dp_8, vertical = Dimens.dp_2),
                )
            }
        }
    }
}

@Composable
private fun Engagement(icon: androidx.compose.ui.graphics.vector.ImageVector, count: Int, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = description },
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(Dimens.dp_6))
        Text(
            count.toString(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyFeed(showSampleButton: Boolean, onLoadSamples: () -> Unit) {
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.dp_24),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconTile(
                icon = Icons.Outlined.Forum,
                size = Dimens.dp_44,
                containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = Dimens.dp_22,
            )
            Text(
                stringResource(R.string.community_empty_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = Dimens.dp_12),
            )
            Text(
                stringResource(R.string.community_coming_soon),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.dp_4),
            )
            if (showSampleButton) {
                TextButton(onClick = onLoadSamples, modifier = Modifier.padding(top = Dimens.dp_4)) {
                    Text(
                        stringResource(R.string.community_load_samples),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ModerationNote() {
    Surface(
        shape = RoundedCornerShape(Dimens.dp_16),
        color = LocalSteadyExtendedColors.current.surfaceContainerGreen.copy(alpha = 0.6f),
        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(Dimens.dp_14), verticalAlignment = Alignment.CenterVertically) {
            IconTile(
                icon = Icons.Outlined.Spa,
                size = Dimens.dp_32,
                containerColor = Color.White,
                iconTint = MaterialTheme.colorScheme.primary,
                iconSize = Dimens.dp_18,
            )
            Text(
                stringResource(R.string.community_moderation_note),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Dimens.dp_12),
            )
        }
    }
}

@Preview(name = "Community", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun CommunityScreenPreview() {
    val now = System.currentTimeMillis()
    AppTheme(darkTheme = false) {
        CommunityContent(state = CommunityUiState(posts = debugSamplePosts(now), nowMillis = now), onFilter = {}, onLoadSamples = {})
    }
}

@Preview(name = "Community - empty", showBackground = true, widthDp = 390, heightDp = 700)
@Composable
private fun CommunityEmptyPreview() {
    AppTheme(darkTheme = false) {
        CommunityContent(state = CommunityUiState(canLoadSamples = true), onFilter = {}, onLoadSamples = {})
    }
}
