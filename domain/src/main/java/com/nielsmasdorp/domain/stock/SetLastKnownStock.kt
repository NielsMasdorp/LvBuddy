package com.nielsmasdorp.domain.stock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetLastKnownStock(private val lastKnownStockRepository: LastKnownStockRepository) {

    suspend fun invoke(productId: String, stock: Int) = withContext(Dispatchers.IO) {
        lastKnownStockRepository.saveLastKnownStock(productId, stock)
    }
}