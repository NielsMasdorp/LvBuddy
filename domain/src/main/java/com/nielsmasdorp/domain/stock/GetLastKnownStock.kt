package com.nielsmasdorp.domain.stock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLastKnownStock(private val lastKnownStockRepository: LastKnownStockRepository) {

    suspend fun invoke(productId: String): Int = withContext(Dispatchers.IO) {
        lastKnownStockRepository.getLastKnownStock(productId)
    }
}