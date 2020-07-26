package com.nielsmasdorp.lvbnotifier.data.notifications.system

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nielsmasdorp.domain.notifications.NotificationHandler
import com.nielsmasdorp.lvbnotifier.LVBNotifierApplication.Companion.NOTIFICATION_CHANNEL_ID
import com.nielsmasdorp.lvbnotifier.R
import com.nielsmasdorp.lvbnotifier.presentation.stock.StockActivity
import java.util.*

class SystemNotificationHandler(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat
) : NotificationHandler {

    override suspend fun send(productName: String, text: String) {
        val intent = Intent(context, StockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_lvb_notification)
            .setContentTitle(productName)
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        notificationManager.notify(Random().nextInt(1_000_000), builder.build())
    }
}