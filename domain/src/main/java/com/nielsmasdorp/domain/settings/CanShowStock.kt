package com.nielsmasdorp.domain.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CanShowStock(
    private val settings: SettingsRepository
) {

    suspend fun invoke(): Boolean = withContext(Dispatchers.IO) {
        settings.getRetailerApiClientId().isNotBlank() &&
                settings.getRetailerApiClientSecret().isNotBlank()
    }
}