package com.nielsmasdorp.domain.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CanSendBuyBoxNotifications(
    private val settings: SettingsRepository,
    private val canSendNotifications: CanSendNotifications
) {

    suspend fun invoke(): Boolean = withContext(Dispatchers.IO) {
        canSendNotifications.invoke() && settings.getSellerName().isNotBlank()
    }
}