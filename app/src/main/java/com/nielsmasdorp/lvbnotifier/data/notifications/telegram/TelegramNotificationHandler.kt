package com.nielsmasdorp.lvbnotifier.data.notifications.telegram

import com.nielsmasdorp.domain.notifications.NotificationHandler
import com.nielsmasdorp.domain.settings.SettingsRepository
import com.nielsmasdorp.lvbnotifier.data.notifications.telegram.network.TelegramService

class TelegramNotificationHandler(
    private val telegramService: TelegramService,
    private val settingsRepository: SettingsRepository
) : NotificationHandler {

    override suspend fun send(productName: String, text: String) {
        telegramService.sendMessage(
            message = text,
            botToken = settingsRepository.getTelegramBotToken(),
            chatId = settingsRepository.getTelegramChatId()
        )
    }
}