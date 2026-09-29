package com.steady.app.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.steady.app.R
import com.steady.app.ui.components.MiniStatCard
import com.steady.app.ui.components.PageDotsIndicator
import com.steady.app.ui.components.PremiumBadge
import com.steady.app.ui.theme.LocalAppSpacing
import com.steady.app.ui.theme.LocalSteadyExtendedColors
import com.steady.app.ui.theme.SteadyShapes
import kotlinx.coroutines.launch

private data class OnboardingPage(val titleRes: Int, val bodyRes: Int)

private val pages = listOf(
    OnboardingPage(R.string.onboarding_welcome_title, R.string.onboarding_welcome_body),
    OnboardingPage(R.string.onboarding_scan_title, R.string.onboarding_scan_body),
    OnboardingPage(R.string.onboarding_progress_title, R.string.onboarding_progress_body),
)

private object WelcomeDesignColors {
    val Background = Color(0xFFFCF9F3)
    val Primary = Color(0xFF154539)
    val PrimaryContainer = Color(0xFF2F5D50)
    val PrimaryFixed = Color(0xFFBCEDDC)
    val Secondary = Color(0xFF934A21)
    val SecondaryContainer = Color(0xFFFD9F6E)
    val Tertiary = Color(0xFF06425C)
    val TertiaryContainer = Color(0xFF285A74)
    val OnSurfaceVariant = Color(0xFF404945)
    val ProgressTrack = Color(0xFFEBE8E2)
    val InactiveDot = Color(0xFFD5DDD2)
}

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
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (pagerState.currentPage == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LocalSteadyExtendedColors.current.surfaceContainerGreen,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.size(32.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = WelcomeDesignColors.Primary,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 20.sp,
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
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                            color = WelcomeDesignColors.OnSurfaceVariant,
                        )
                    }
                } else {
                    Spacer(Modifier.size(1.dp))
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
                val page = pages[index]
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(Modifier.height(LocalAppSpacing.current.extraSmall))
                    when (index) {
                        0 -> WelcomeHero()
                        1 -> ScanMealsHero()
                        else -> ProgressHero()
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(
                        stringResource(page.titleRes),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        stringResource(page.bodyRes),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 24.sp),
                        color = WelcomeDesignColors.OnSurfaceVariant,
                        textAlign = TextAlign.Start,
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
                    .padding(vertical = 24.dp),
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
                    .height(56.dp),
                shape = SteadyShapes.button,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = WelcomeDesignColors.PrimaryContainer,
                ),
            ) {
                Text(stringResource(if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next))
                Spacer(Modifier.width(LocalAppSpacing.current.small))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalAppSpacing.current.medium),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    stringResource(R.string.onboarding_have_account) + " ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = WelcomeDesignColors.OnSurfaceVariant,
                )
                Text(
                    stringResource(R.string.sign_in_action),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = WelcomeDesignColors.Primary,
                    modifier = Modifier.clickable { viewModel.markCompleted(onSignIn) },
                )
            }
        }
    }
}

@Composable
private fun HeroCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SteadyShapes.hero,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(Modifier.padding(LocalAppSpacing.current.medium), content = content)
    }
}

@Composable
private fun WelcomeHero() {
    val extended = LocalSteadyExtendedColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SteadyShapes.hero,
        color = extended.surfaceContainerGreen,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = SteadyShapes.iconTile,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.size(28.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Spa,
                                contentDescription = null,
                                tint = WelcomeDesignColors.Primary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(
                            stringResource(R.string.onboarding_hero_journal_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = WelcomeDesignColors.Primary,
                        )
                        Text(
                            stringResource(R.string.onboarding_hero_journal_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
                            color = WelcomeDesignColors.OnSurfaceVariant,
                        )
                    }
                }
                Surface(
                    shape = SteadyShapes.chip,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(WelcomeDesignColors.PrimaryContainer, CircleShape),
                        )
                        Text(
                            stringResource(R.string.onboarding_hero_active),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.sp,
                            ),
                            color = WelcomeDesignColors.OnSurfaceVariant,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

            Spacer(Modifier.height(12.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = extended.surfaceContainerGreen,
                        modifier = Modifier.size(20.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Medication,
                                contentDescription = null,
                                tint = WelcomeDesignColors.Primary,
                                modifier = Modifier.size(13.dp),
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.onboarding_hero_next_dose),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    Text(
                        " · Thu 9:00 AM",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = WelcomeDesignColors.OnSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Surface(
                        shape = CircleShape,
                        color = WelcomeDesignColors.PrimaryFixed,
                        modifier = Modifier.size(16.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = WelcomeDesignColors.Primary,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanMealsHero() {
    val extended = LocalSteadyExtendedColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f),
            shape = SteadyShapes.hero,
            color = extended.surfaceContainerOrange,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(88.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(LocalAppSpacing.current.medium))
        Surface(shape = SteadyShapes.chip, color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
            Text(
                stringResource(R.string.onboarding_hero_scan_tag),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = LocalAppSpacing.current.medium, vertical = LocalAppSpacing.current.small),
            )
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
