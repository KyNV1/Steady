package com.steady.app.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.steady.app.R
import com.steady.app.ui.components.IconTile
import com.steady.app.ui.components.MiniStatCard
import com.steady.app.ui.components.PageDotsIndicator
import com.steady.app.ui.components.PremiumBadge
import com.steady.app.ui.components.SteadyCard
import com.steady.app.ui.theme.Dimens
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.OnboardingBodyStyle
import com.steady.app.ui.theme.OnboardingHeadlineStyle
import com.steady.app.ui.theme.SteadyShapes
import com.steady.app.ui.theme.WelcomeDesignColors
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

private data class OnboardingPage(val titleRes: Int, val bodyRes: Int)

private val pages = listOf(
    OnboardingPage(R.string.onboarding_welcome_title, R.string.onboarding_welcome_body),
    OnboardingPage(R.string.onboarding_scan_title, R.string.onboarding_scan_body),
    OnboardingPage(R.string.onboarding_progress_title, R.string.onboarding_progress_body),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    onSignIn: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Scaffold(containerColor = WelcomeDesignColors.Background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = LocalAppSpacing.current.screenPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp_56),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (pagerState.currentPage == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(
                            icon = Icons.Default.Spa,
                            size = Dimens.dp_32,
                            containerColor = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                            iconTint = WelcomeDesignColors.Primary,
                            iconSize = Dimens.dp_20,
                            border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                        )
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = Dimens.sp_20,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = WelcomeDesignColors.Primary,
                            modifier = Modifier.padding(start = LocalAppSpacing.current.small),
                        )
                    }
                } else {
                    IconButton(onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }

                if (!isLastPage) {
                    TextButton(onClick = { viewModel.markCompleted(onFinished) }) {
                        Text(
                            stringResource(R.string.onboarding_skip),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = Dimens.sp_14),
                            color = WelcomeDesignColors.OnSurfaceVariant,
                        )
                    }
                } else {
                    Spacer(Modifier.size(Dimens.dp_1))
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
                val page = pages[index]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = pagerState.getOffsetDistanceInPages(index).absoluteValue.coerceIn(0f, 1f)
                            alpha = lerp(0.4f, 1f, 1f - pageOffset)
                            scaleY = lerp(0.92f, 1f, 1f - pageOffset)
                            translationX = size.width * pageOffset * 0.08f * if (index > pagerState.currentPage) 1f else -1f
                        },
                ) {
                    Spacer(Modifier.height(LocalAppSpacing.current.extraSmall))
                    when (index) {
                        0 -> WelcomeHero()
                        1 -> ScanMealsHero()
                        else -> ProgressHero()
                    }
                    Spacer(Modifier.height(Dimens.dp_28))
                    // Stitch centers the copy block on every onboarding page except Welcome (text-left).
                    val copyAlign = if (index == 0) TextAlign.Start else TextAlign.Center
                    Text(
                        stringResource(page.titleRes),
                        style = OnboardingHeadlineStyle,
                        textAlign = copyAlign,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        stringResource(page.bodyRes),
                        style = OnboardingBodyStyle,
                        color = WelcomeDesignColors.OnSurfaceVariant,
                        textAlign = copyAlign,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = LocalAppSpacing.current.small),
                    )
                }
            }

            PageDotsIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.dp_24),
                activeColor = WelcomeDesignColors.PrimaryContainer,
                inactiveColor = WelcomeDesignColors.InactiveDot,
            )

            Button(
                onClick = {
                    if (isLastPage) {
                        viewModel.markCompleted(onFinished)
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp_50),
                shape = SteadyShapes.button,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = WelcomeDesignColors.PrimaryContainer,
                ),
            ) {
                Text(stringResource(if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalAppSpacing.current.medium),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.onboarding_have_account) + " ",
                    fontSize = Dimens.sp_13,
                    color = WelcomeDesignColors.OnSurfaceVariant,
                )
                Text(
                    stringResource(R.string.sign_in_action),
                    fontSize = Dimens.sp_13,
                    fontWeight = FontWeight.Bold,
                    color = WelcomeDesignColors.Primary,
                    modifier = Modifier.clickable { viewModel.markCompleted(onSignIn) },
                )
            }
        }
    }
}

@Composable
private fun HeroCard(content: @Composable ColumnScope.() -> Unit) {
    SteadyCard(modifier = Modifier.fillMaxWidth(), shape = SteadyShapes.hero) {
        Column(Modifier.padding(LocalAppSpacing.current.medium), content = content)
    }
}

@Composable
private fun WelcomeHero() {
    val extended = LocalSteadyExtendedColors.current
    SteadyCard(modifier = Modifier.fillMaxWidth(), shape = SteadyShapes.hero, color = extended.surfaceContainerGreen) {
        Column(Modifier.padding(Dimens.dp_20)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(
                        icon = Icons.Default.Spa,
                        size = Dimens.dp_28,
                        shape = SteadyShapes.iconTile,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        iconTint = WelcomeDesignColors.Primary,
                        iconSize = Dimens.dp_16,
                        border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                    )
                    Column(Modifier.padding(start = Dimens.dp_8)) {
                        Text(
                            stringResource(R.string.onboarding_hero_journal_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 15.sp,
                                lineHeight = Dimens.sp_18,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = WelcomeDesignColors.Primary,
                        )
                        Text(
                            stringResource(R.string.onboarding_hero_journal_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.sp_11, lineHeight = Dimens.sp_14),
                            color = WelcomeDesignColors.OnSurfaceVariant,
                        )
                    }
                }
                Surface(
                    shape = SteadyShapes.chip,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Dimens.dp_10, vertical = Dimens.dp_4),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(Dimens.dp_8)
                                .background(WelcomeDesignColors.PrimaryContainer, CircleShape),
                        )
                        Text(
                            stringResource(R.string.onboarding_hero_active),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = Dimens.sp_10,
                                lineHeight = Dimens.sp_14,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.sp,
                            ),
                            color = WelcomeDesignColors.OnSurfaceVariant,
                            modifier = Modifier.padding(start = Dimens.dp_6),
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.dp_16))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.dp_10)) {
                MiniStatCard(
                    icon = Icons.Default.FitnessCenter,
                    accentColor = WelcomeDesignColors.Secondary,
                    accentTint = extended.surfaceContainerOrange,
                    progressColor = WelcomeDesignColors.SecondaryContainer,
                    trackColor = WelcomeDesignColors.ProgressTrack,
                    label = stringResource(R.string.macro_protein),
                    valueNumber = "68",
                    valueUnit = "g",
                    badgeText = "+4g",
                    progress = 0.72f,
                    modifier = Modifier.weight(1f),
                )
                MiniStatCard(
                    icon = Icons.Default.WaterDrop,
                    accentColor = WelcomeDesignColors.Tertiary,
                    accentTint = Color(0xFFE3EEF6),
                    progressColor = WelcomeDesignColors.TertiaryContainer,
                    trackColor = WelcomeDesignColors.ProgressTrack,
                    label = stringResource(R.string.daily_goals_water_label),
                    valueNumber = "1.8",
                    valueUnit = "L",
                    badgeText = stringResource(R.string.onboarding_hero_water_badge),
                    progress = 0.85f,
                    modifier = Modifier.weight(1f),
                )
                MiniStatCard(
                    icon = Icons.Default.CheckCircle,
                    accentColor = WelcomeDesignColors.Primary,
                    accentTint = extended.surfaceContainerGreen,
                    progressColor = WelcomeDesignColors.PrimaryContainer,
                    trackColor = WelcomeDesignColors.ProgressTrack,
                    label = stringResource(R.string.onboarding_hero_target_label),
                    valueNumber = "82",
                    valueUnit = "%",
                    badgeText = stringResource(R.string.onboarding_hero_target_badge),
                    progress = 0.82f,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Dimens.dp_12))
            SteadyCard(shape = CircleShape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.dp_14, vertical = Dimens.dp_8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconTile(
                        icon = Icons.Default.Medication,
                        size = Dimens.dp_20,
                        containerColor = extended.surfaceContainerGreen,
                        iconTint = WelcomeDesignColors.Primary,
                        iconSize = 13.dp,
                    )
                    Text(
                        stringResource(R.string.onboarding_hero_next_dose),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = Dimens.sp_12,
                            lineHeight = Dimens.sp_16,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = Dimens.dp_8),
                    )
                    Text(
                        " · Thu 9:00 AM",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = Dimens.sp_12,
                            lineHeight = Dimens.sp_16,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = WelcomeDesignColors.OnSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    IconTile(
                        icon = Icons.Default.Check,
                        size = Dimens.dp_16,
                        containerColor = WelcomeDesignColors.PrimaryFixed,
                        iconTint = WelcomeDesignColors.Primary,
                        iconSize = Dimens.dp_12,
                    )
                }
            }
        }
    }
}

private enum class ViewfinderCorner { TopStart, TopEnd, BottomStart, BottomEnd }

/** One L-shaped camera-viewfinder corner mark, matching Stitch's scan hero bracket style. */
@Composable
private fun ViewfinderBracket(corner: ViewfinderCorner, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier = modifier.size(Dimens.dp_16)) {
        val strokeWidth = Dimens.dp_2.toPx()
        val edge = size.minDimension
        val horizontalY = if (corner == ViewfinderCorner.TopStart || corner == ViewfinderCorner.TopEnd) 0f else edge
        val verticalX = if (corner == ViewfinderCorner.TopStart || corner == ViewfinderCorner.BottomStart) 0f else edge
        drawLine(color, Offset(0f, horizontalY), Offset(edge, horizontalY), strokeWidth, cap = StrokeCap.Round)
        drawLine(color, Offset(verticalX, 0f), Offset(verticalX, edge), strokeWidth, cap = StrokeCap.Round)
    }
}

@Composable
private fun ScanMealsHero() {
    val extended = LocalSteadyExtendedColors.current
    SteadyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = SteadyShapes.hero,
        color = extended.surfaceContainerOrange,
        border = BorderStroke(Dimens.dp_1, extended.surfaceContainerOrangeAlt),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.dp_24),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.size(190.dp), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(Dimens.dp_16),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = BorderStroke(Dimens.dp_1, extended.outlineSubtle),
                            modifier = Modifier.size(96.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                IconTile(
                                    icon = Icons.Outlined.Restaurant,
                                    size = Dimens.dp_64,
                                    containerColor = extended.premiumBg,
                                    iconTint = MaterialTheme.colorScheme.secondary,
                                    iconSize = Dimens.dp_32,
                                )
                            }
                        }
                        // Pulsing scanner-beam line crossing the plate circle, per the Stitch hero.
                        Box(
                            modifier = Modifier
                                .width(112.dp)
                                .height(Dimens.dp_2)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
                        )
                    }
                }
                ViewfinderBracket(ViewfinderCorner.TopStart, Modifier.align(Alignment.TopStart).padding(Dimens.dp_12))
                ViewfinderBracket(ViewfinderCorner.TopEnd, Modifier.align(Alignment.TopEnd).padding(Dimens.dp_12))
                ViewfinderBracket(ViewfinderCorner.BottomStart, Modifier.align(Alignment.BottomStart).padding(Dimens.dp_12))
                ViewfinderBracket(ViewfinderCorner.BottomEnd, Modifier.align(Alignment.BottomEnd).padding(Dimens.dp_12))
                IconTile(
                    icon = Icons.Default.CenterFocusStrong,
                    size = Dimens.dp_22,
                    containerColor = extended.surfaceContainerOrangeAlt,
                    iconTint = MaterialTheme.colorScheme.secondary,
                    iconSize = Dimens.dp_14,
                    border = BorderStroke(Dimens.dp_1, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = Dimens.dp_4, y = -Dimens.dp_4),
                )
            }

            Spacer(Modifier.height(Dimens.dp_16))
            SteadyCard(shape = SteadyShapes.chip) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = Dimens.dp_14, vertical = Dimens.dp_6),
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(Dimens.dp_14),
                    )
                    Text(
                        stringResource(R.string.onboarding_hero_scan_tag),
                        style = MaterialTheme.typography.labelSmall,
                        color = WelcomeDesignColors.Primary,
                        modifier = Modifier.padding(start = Dimens.dp_6),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressHero() {
    HeroCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    stringResource(R.string.onboarding_hero_trend),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = LocalAppSpacing.current.small),
                )
            }
            PremiumBadge()
        }
        Spacer(Modifier.height(LocalAppSpacing.current.cardGap))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.cardGap)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Column(Modifier.padding(start = LocalAppSpacing.current.small)) {
                    Text("12", style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.onboarding_hero_streak_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("-8 lb", style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(R.string.onboarding_hero_since_start),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
