package com.nielsmasdorp.lvbnotifier.data.buybox.network

import retrofit2.http.GET
import retrofit2.http.Query

interface BuyBoxService {

    @GET("text")
    suspend fun getProductPageHtml(@Query("textToBeParsed") ean: String): String
}