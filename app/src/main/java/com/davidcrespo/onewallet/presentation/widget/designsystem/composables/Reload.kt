package com.davidcrespo.onewallet.presentation.widget.designsystem.composables

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
import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.presentation.widget.GetWidgetCallback

@Composable
fun Reload(modifier: GlanceModifier = GlanceModifier) {
    Box(
        modifier = modifier
            .size(30.dp)
            .background(ImageProvider(R.drawable.widget_round_background))
            .clickable(actionRunCallback<GetWidgetCallback>()),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_refresh),
            contentDescription = LocalContext.current.getString(R.string.reload_cd),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
        )
    }
}
