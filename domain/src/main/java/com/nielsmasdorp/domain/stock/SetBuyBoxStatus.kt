package com.nielsmasdorp.domain.stock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetBuyBoxStatus(private val lastKnownStockRepository: LastKnownStockRepository) {

    suspend fun invoke(productId: String, hasBuyBox: Boolean) = withContext(Dispatchers.IO) {
        lastKnownStockRepository.saveBuyBoxStatus(productId, hasBuyBox)
    }
}