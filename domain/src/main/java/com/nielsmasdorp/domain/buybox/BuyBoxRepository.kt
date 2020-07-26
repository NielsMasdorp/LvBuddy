package com.nielsmasdorp.domain.buybox

interface BuyBoxRepository {

    suspend fun hasBuyBox(ean: String): Boolean
}