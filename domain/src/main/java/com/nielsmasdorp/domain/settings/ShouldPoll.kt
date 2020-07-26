package com.nielsmasdorp.domain.settings

class ShouldPoll(
    private val settings: SettingsRepository,
    private val canSendNotifications: CanSendNotifications
) {

    suspend fun invoke(): Boolean {
        val poll = settings.isOrderNotificationsEnabled() || settings.isBuyBoxNotificationsEnabled()
        return poll && canSendNotifications.invoke()
    }
}