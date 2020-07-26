package com.nielsmasdorp.domain.settings

interface SettingsRepository {

    suspend fun isBuyBoxNotificationsEnabled(): Boolean

    suspend fun setBuyBoxNotificationsEnabled(enabled: Boolean)

    suspend fun isOrderNotificationsEnabled(): Boolean

    suspend fun setOrderNotificationsEnabled(enabled: Boolean)

    suspend fun getSelectedNotificationType(): NotificationType

    suspend fun getTelegramBotToken(): String

    suspend fun getTelegramChatId(): String

    suspend fun getRetailerApiClientId(): String

    suspend fun getRetailerApiClientSecret(): String

    suspend fun getSellerName(): String

    suspend fun getPollFrequencyMinutes(): Int

    companion object {

        const val BUY_BOX_NOTIFICATIONS_ENABLED_KEY = "buy_box_notifications"
        const val ORDER_NOTIFICATIONS_ENABLED_KEY = "order_notifications"
        const val TELEGRAM_BOT_TOKEN = "telegram_bot_token"
        const val TELEGRAM_CHAT_ID = "telegram_chat_id"
        const val RETAILER_CLIENT_ID = "bol_client_id"
        const val RETAILER_CLIENT_SECRET = "bol_client_secret"
        const val SELLER_NAME = "bol_seller_name"
        const val POLL_FREQUENCY = "bol_poll_frequency"
        const val NOTIFICATIONS_TYPE = "notifications_type"

        val CREDENTIALS = listOf(
            TELEGRAM_BOT_TOKEN, TELEGRAM_CHAT_ID, RETAILER_CLIENT_ID, RETAILER_CLIENT_SECRET
        )

        val NOTIFICATIONS = listOf(
            BUY_BOX_NOTIFICATIONS_ENABLED_KEY, ORDER_NOTIFICATIONS_ENABLED_KEY
        )
    }
}