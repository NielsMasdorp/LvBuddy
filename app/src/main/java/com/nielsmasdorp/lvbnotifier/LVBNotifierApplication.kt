package com.nielsmasdorp.lvbnotifier

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.nielsmasdorp.lvbnotifier.di.appModule
import com.nielsmasdorp.lvbnotifier.work.StateUpdateScheduler
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

class LVBNotifierApplication : Application() {

    private val notificationScheduler: StateUpdateScheduler by inject()

    private val notificationManager: NotificationManagerCompat by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        setupNotificationChannel()
        notificationScheduler.schedule()
    }

    private fun setupNotificationChannel() {
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {

        const val NOTIFICATION_CHANNEL_ID = "80085"
    }
}