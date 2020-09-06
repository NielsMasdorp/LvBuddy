package com.nielsmasdorp.lvbnotifier.data.buybox

import com.nielsmasdorp.domain.buybox.BuyBoxRepository
import com.nielsmasdorp.domain.settings.SettingsRepository
import com.nielsmasdorp.lvbnotifier.data.buybox.network.BuyBoxService

/**
 * Scrapes the bol product page in order to find if we have the buy box
 */
class ScrapeBuyBoxRepository(
    private val buyBoxService: BuyBoxService,
    private val settingsRepository: SettingsRepository
) : BuyBoxRepository {

    override suspend fun hasBuyBox(ean: String): Boolean {
        val sellerName = settingsRepository.getSellerName()
        if (sellerName.isBlank()) return false
        return buyBoxService.getProductPageHtml(ean).contains(getPageData(sellerName))
    }


    private fun getPageData(sellerName: String): String {
        return " data-modal-size=\"full\">\n" +
                "          $sellerName"
    }
}