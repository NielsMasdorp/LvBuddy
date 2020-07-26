package com.nielsmasdorp.domain.stock

interface LastKnownStockRepository {

    suspend fun getLastKnownStock(productId: String): Int

    suspend fun saveLastKnownStock(productId: String, stock: Int)

    suspend fun getBuyBoxStatus(productId: String): Boolean?

    suspend fun saveBuyBoxStatus(productId: String, hasBuyBox: Boolean)
}