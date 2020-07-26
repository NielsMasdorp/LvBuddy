package com.nielsmasdorp.domain.stock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetBuyBoxStatus(private val lastKnownStockRepository: LastKnownStockRepository) {

    suspend fun invoke(productId: String): Boolean? = withContext(Dispatchers.IO) {
        lastKnownStockRepository.getBuyBoxStatus(productId)
    }
}