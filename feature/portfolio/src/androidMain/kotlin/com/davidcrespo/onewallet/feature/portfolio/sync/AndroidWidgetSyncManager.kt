package com.davidcrespo.onewallet.feature.portfolio.sync

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class AndroidWidgetSyncManager(
    private val context: Context
) : WidgetSyncManager {
    override fun updateWidgets() {
        try {
            @Suppress("UNCHECKED_CAST")
            val workerClass = Class.forName("com.davidcrespo.onewallet.feature.widget.WidgetsRefreshWorker")
                    as Class<out ListenableWorker>
            val req = OneTimeWorkRequest.Builder(workerClass)
                .addTag("manual_widgets_refresh")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "manual_widgets_refresh",
                ExistingWorkPolicy.REPLACE,
                req
            )
        } catch (_: Exception) {
            // Widget module not available
        }
    }
}
