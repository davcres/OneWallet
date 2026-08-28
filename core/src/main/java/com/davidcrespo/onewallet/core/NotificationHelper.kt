package com.davidcrespo.onewallet.core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.davidcrespo.onewallet.core.R

object NotificationHelper {
    private const val CHANNEL_ID = "price_alerts_channel"
    private const val CHANNEL_NAME = "Price Alerts"
    private const val CHANNEL_DESCRIPTION = "Notifications for price changes in your portfolio"

    fun showPriceAlertNotification(context: Context, symbol: String, name: String, changePercent: Double) {
        createNotificationChannel(context)

        val intent = (context.packageManager.getLaunchIntentForPackage(context.packageName) ?: Intent(Intent.ACTION_MAIN)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = if (changePercent >= 0) {
            context.getString(R.string.notification_price_up_title, symbol)
        } else {
            context.getString(R.string.notification_price_down_title, symbol)
        }

        val body = context.getString(
            R.string.notification_price_body,
            name.ifEmpty { symbol },
            "%.2f".format(changePercent)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // notificationId is a unique int for each notification that you must define.
                // Using symbol hash to group notifications by asset or just use a random one.
                NotificationManagerCompat.from(context)
                    .notify(symbol.hashCode(), builder.build())
            }
        } else {
            NotificationManagerCompat.from(context)
                .notify(symbol.hashCode(), builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
