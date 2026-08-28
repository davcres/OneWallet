package com.davidcrespo.onewallet.feature.widget.designsystem.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.size
import com.davidcrespo.onewallet.core.R
import com.davidcrespo.onewallet.feature.widget.GetWidgetCallback

@Composable
fun Reload(
    isDarkTheme: Boolean,
    modifier: GlanceModifier = GlanceModifier
) {
    val background = if (isDarkTheme) R.drawable.widget_round_background_dark else R.drawable.widget_round_background_light
    Box(
        modifier = modifier
            .size(30.dp)
            .background(ImageProvider(background))
            .clickable(actionRunCallback<GetWidgetCallback>()),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_refresh),
            contentDescription = LocalContext.current.getString(R.string.reload_cd),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary)
        )
    }
}
