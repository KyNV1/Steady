package com.steady.app.feature.community

import androidx.lifecycle.ViewModel
import com.steady.app.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class PostCategory { TIPS_RECIPES, MILESTONES, SIDE_EFFECTS }

data class CommunityPost(
    val id: String,
    val authorName: String,
    /** e.g. "Tirzepatide · wk 9"; shown as the author's context badge. */
    val medicationBadge: String,
    val body: String,
    val createdAtEpochMillis: Long,
    val likeCount: Int,
    val commentCount: Int,
    val category: PostCategory,
    val hashtag: String,
)

data class CommunityUiState(
    /** Null = "All posts". */
    val filter: PostCategory? = null,
    val posts: List<CommunityPost> = emptyList(),
    val nowMillis: Long = System.currentTimeMillis(),
    val canLoadSamples: Boolean = BuildConfig.DEBUG,
) {
    val visiblePosts: List<CommunityPost> get() = if (filter == null) posts else posts.filter { it.category == filter }
}

/**
 * Read-only Community shell. There's no backend feed yet and the plan keeps posting disabled until
 * moderation, reporting and abuse limits exist, so release builds always show the empty state.
 */
@HiltViewModel
class CommunityViewModel @Inject constructor() : ViewModel() {
    private val mutableState = MutableStateFlow(CommunityUiState())
    val state: StateFlow<CommunityUiState> = mutableState.asStateFlow()

    fun setFilter(filter: PostCategory?) = mutableState.update { it.copy(filter = filter) }

    /** Debug-only: shows the Stitch design's sample posts so the feed layout can be reviewed. */
    fun loadSamples() {
        if (!BuildConfig.DEBUG) return
        val now = System.currentTimeMillis()
        mutableState.update { it.copy(posts = debugSamplePosts(now), nowMillis = now) }
    }
}

internal fun debugSamplePosts(nowMillis: Long): List<CommunityPost> {
    fun hoursAgo(h: Long) = nowMillis - TimeUnit.HOURS.toMillis(h)
    return listOf(
        CommunityPost(
            id = "debug-post-1",
            authorName = "Maria K.",
            medicationBadge = "Tirzepatide · wk 9",
            body = "Hit my 90g protein goal today without feeling overly full! Sautéed chicken skewers with cucumber dill " +
                "yogurt have been an absolute game changer on days when appetite is completely muted.",
            createdAtEpochMillis = hoursAgo(2),
            likeCount = 24,
            commentCount = 6,
            category = PostCategory.TIPS_RECIPES,
            hashtag = "#ProteinTips",
        ),
        CommunityPost(
            id = "debug-post-2",
            authorName = "David L.",
            medicationBadge = "Semaglutide · wk 16",
            body = "Week 16 milestone check-in: down 22 lbs, but the biggest win is having steady morning energy without " +
                "the 3pm crash. Remember to drink your electrolytes, especially on injection morning!",
            createdAtEpochMillis = hoursAgo(4),
            likeCount = 41,
            commentCount = 11,
            category = PostCategory.MILESTONES,
            hashtag = "#Milestone",
        ),
    )
}
