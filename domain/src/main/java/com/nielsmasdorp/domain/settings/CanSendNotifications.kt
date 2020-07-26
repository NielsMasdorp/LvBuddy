package com.nielsmasdorp.domain.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CanSendNotifications(private val settings: SettingsRepository) {

    suspend fun invoke(): Boolean = withContext(Dispatchers.IO) {
        if (settings.getSelectedNotificationType() == NotificationType.TELEGRAM) {
            settings.getTelegramBotToken().isNotBlank() &&
                    settings.getTelegramChatId().isNotBlank() &&
                    settings.getRetailerApiClientId().isNotBlank() &&
                    settings.getRetailerApiClientSecret().isNotBlank()
        } else {
            true
        }
    }
}