package com.nielsmasdorp.lvbnotifier.data.settings

import com.nielsmasdorp.domain.settings.NotificationType
import com.nielsmasdorp.domain.settings.SettingsRepository
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.BUY_BOX_NOTIFICATIONS_ENABLED_KEY
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.NOTIFICATIONS_TYPE
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.ORDER_NOTIFICATIONS_ENABLED_KEY
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.POLL_FREQUENCY
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.RETAILER_CLIENT_ID
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.RETAILER_CLIENT_SECRET
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.SELLER_NAME
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.TELEGRAM_BOT_TOKEN
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.TELEGRAM_CHAT_ID
import com.nielsmasdorp.lvbnotifier.util.SharedPrefsDataStore

class SharedPreferencesSettingsRepository(private val preferenceDataStore: SharedPrefsDataStore) :
    SettingsRepository {

    override suspend fun isBuyBoxNotificationsEnabled(): Boolean {
        return preferenceDataStore.getBoolean(BUY_BOX_NOTIFICATIONS_ENABLED_KEY, false)
    }

    override suspend fun setBuyBoxNotificationsEnabled(enabled: Boolean) {
        preferenceDataStore.putBoolean(BUY_BOX_NOTIFICATIONS_ENABLED_KEY, enabled)
    }

    override suspend fun isOrderNotificationsEnabled(): Boolean {
        return preferenceDataStore.getBoolean(ORDER_NOTIFICATIONS_ENABLED_KEY, false)
    }

    override suspend fun setOrderNotificationsEnabled(enabled: Boolean) {
        preferenceDataStore.putBoolean(ORDER_NOTIFICATIONS_ENABLED_KEY, enabled)
    }

    override suspend fun getSelectedNotificationType(): NotificationType {
        val selected = preferenceDataStore.getString(NOTIFICATIONS_TYPE, "0")
        return NotificationType.fromValue(selected!!) ?: NotificationType.SYSTEM
    }

    override suspend fun getTelegramBotToken(): String {
        return preferenceDataStore.getString(TELEGRAM_BOT_TOKEN, "")!!
    }

    override suspend fun getTelegramChatId(): String {
        return preferenceDataStore.getString(TELEGRAM_CHAT_ID, "")!!
    }

    override suspend fun getRetailerApiClientId(): String {
        return preferenceDataStore.getString(RETAILER_CLIENT_ID, "")!!
    }

    override suspend fun getRetailerApiClientSecret(): String {
        return preferenceDataStore.getString(RETAILER_CLIENT_SECRET, "")!!
    }

    override suspend fun getSellerName(): String {
        return preferenceDataStore.getString(SELLER_NAME, "")!!
    }

    override suspend fun getPollFrequencyMinutes(): Int {
        return preferenceDataStore.getString(POLL_FREQUENCY, DEFAULT_POLL_FREQUENCY_MINUTES)!!
            .toInt()
    }

    companion object {

        private const val DEFAULT_POLL_FREQUENCY_MINUTES = "30"
    }
}