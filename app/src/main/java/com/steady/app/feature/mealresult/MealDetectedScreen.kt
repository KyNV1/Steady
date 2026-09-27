package com.steady.app.feature.mealresult

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.steady.app.R
import com.steady.app.ui.components.ComingSoonContent
import com.steady.app.ui.components.ScreenScaffold

@Composable
fun MealDetectedScreen(onBack: () -> Unit) {
    ScreenScaffold(title = stringResource(R.string.meal_detected_title), onBack = onBack) {
        ComingSoonContent(stringResource(R.string.meal_detected_coming_soon))
    }
}
