package com.nielsmasdorp.lvbnotifier.data.notifications

import android.content.Context
import com.nielsmasdorp.domain.notifications.MessageProvider
import com.nielsmasdorp.lvbnotifier.R

class AndroidMessageProvider(private val context: Context) : MessageProvider {

    override suspend fun getOrderMessage(): String =
        context.getString(R.string.order_message_text)

    override suspend fun getBuyBoxLostMessage(): String =
        context.getString(R.string.buy_box_lost_message_text)

    override suspend fun getBuyBoxGainedMessage(): String =
        context.getString(R.string.buy_box_gained_message_text)

    override suspend fun getCanShowStockError(): String =
        context.getString(R.string.stocks_no_credentials_error)

    override suspend fun getGenericStockError(): String =
        context.getString(R.string.stocks_generic_error)

    override suspend fun getNotificationsNotAllowedError(): String =
        context.getString(R.string.settings_notifications_error)
}
