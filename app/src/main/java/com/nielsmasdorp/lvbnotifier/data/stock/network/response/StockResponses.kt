package com.nielsmasdorp.lvbnotifier.data.stock.network.response

import com.google.gson.annotations.SerializedName

data class StockResponse(

    @SerializedName("offers")
    val offers: List<SingleStockResponse>
)

data class SingleStockResponse(

    @SerializedName("ean")
    val ean: String,

    @SerializedName("bsku")
    val bsku: String,

    @SerializedName("title")
    val productTitle: String,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("nckStock")
    val nckStock: Int
)