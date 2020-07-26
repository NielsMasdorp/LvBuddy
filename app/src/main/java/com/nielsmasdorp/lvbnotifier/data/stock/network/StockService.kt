package com.nielsmasdorp.lvbnotifier.data.stock.network

import com.nielsmasdorp.lvbnotifier.data.stock.network.response.StockResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface StockService {

    @Headers("Accept: application/vnd.retailer.v3+json")
    @GET("inventory")
    suspend fun getStock(@Query("page") page: Int): StockResponse
}