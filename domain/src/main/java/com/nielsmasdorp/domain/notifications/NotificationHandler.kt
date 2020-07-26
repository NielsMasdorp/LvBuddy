package com.nielsmasdorp.domain.notifications

interface NotificationHandler {

    suspend fun send(productName: String, text: String)
}