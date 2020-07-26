package com.nielsmasdorp.domain.stock

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetStock(
    private val stockRepository: StockRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun invoke(): List<Stock> = withContext(dispatcher) {
        val stock = stockRepository.getLatestStock()
        stock.groupBy { it.id }
            .map { (_, duplicates) ->
                if (duplicates.size == 1) {
                    duplicates.first()
                } else {
                    var amount = 0
                    var last: Stock = duplicates.last()
                    for (duplicate in duplicates) {
                        amount += duplicate.amount
                        if (duplicate == duplicates.last()) {
                            last = last.copy(amount = amount)
                        }
                    }
                    last
                }
            }
            .filter { it.amount > 0 }
            .sortedWith(compareByDescending<Stock> { it.amount }
                .thenBy { it.productTitle })
    }
}

