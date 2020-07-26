package com.nielsmasdorp.domain.stock

interface StockRepository {

    suspend fun getLatestStock(): List<Stock>
}