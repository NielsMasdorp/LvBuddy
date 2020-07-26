package com.nielsmasdorp.domain.notifications

import com.nielsmasdorp.domain.settings.NotificationType
import com.nielsmasdorp.domain.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SendNotification(
    private val telegramNotificationHandler: NotificationHandler,
    private val systemNotificationHandler: NotificationHandler,
    private val settingsRepository: SettingsRepository
) {

    suspend fun invoke(productName: String, text: String) = withContext(Dispatchers.IO) {
        when (settingsRepository.getSelectedNotificationType()) {
            NotificationType.TELEGRAM -> telegramNotificationHandler.send(productName, text)
            NotificationType.SYSTEM -> systemNotificationHandler.send(productName, text)
        }
    }
}