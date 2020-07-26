package com.nielsmasdorp.lvbnotifier.data.stock

import com.nielsmasdorp.domain.buybox.BuyBoxRepository
import com.nielsmasdorp.domain.stock.Stock
import com.nielsmasdorp.domain.stock.StockRepository
import com.nielsmasdorp.lvbnotifier.data.stock.network.StockService
import com.nielsmasdorp.lvbnotifier.data.stock.network.response.SingleStockResponse

class ApiStockRepository(
    private val stockService: StockService,
    private val buyBoxRepository: BuyBoxRepository
) : StockRepository {

    override suspend fun getLatestStock(): List<Stock> {
        return mutableListOf<SingleStockResponse>().apply {
            for (page in 1..20) {
                val stock = stockService.getStock(page)
                if (!stock.offers.isNullOrEmpty()) {
                    addAll(stock.offers)
                } else {
                    break // reached end of stock
                }
            }
        }.map { product ->
            Stock(
                id = product.ean,
                productTitle = product.productTitle,
                amount = product.stock,
                hasBuyBox = if (product.stock > 0) {
                    buyBoxRepository.hasBuyBox(product.ean)
                } else {
                    false
                }
            )
        }
    }
}