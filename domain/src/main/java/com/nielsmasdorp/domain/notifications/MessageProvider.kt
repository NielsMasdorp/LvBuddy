package com.nielsmasdorp.domain.notifications

interface MessageProvider {

    suspend fun getOrderMessage(): String

    suspend fun getBuyBoxLostMessage(): String

    suspend fun getBuyBoxGainedMessage(): String

    suspend fun getCanShowStockError(): String

    suspend fun getGenericStockError(): String

    suspend fun getNotificationsNotAllowedError(): String
}