package com.nielsmasdorp.lvbnotifier.data.notifications.telegram.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TelegramService {

    @GET("/{botToken}/sendMessage")
    suspend fun sendMessage(
        @Path("botToken") botToken: String,
        @Query("chat_id") chatId: String,
        @Query("text") message: String
    )
}