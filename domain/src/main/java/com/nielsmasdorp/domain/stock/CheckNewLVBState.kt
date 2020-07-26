package com.nielsmasdorp.domain.stock

import com.nielsmasdorp.domain.notifications.MessageProvider
import com.nielsmasdorp.domain.notifications.SendNotification
import com.nielsmasdorp.domain.settings.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class CheckNewLVBState(
    private val getStock: GetStock,
    private val getLastKnownStock: GetLastKnownStock,
    private val setLastKnownStock: SetLastKnownStock,
    private val getBuyBoxStatus: GetBuyBoxStatus,
    private val setBuyBoxStatus: SetBuyBoxStatus,
    private val settingsRepository: SettingsRepository,
    private val messageProvider: MessageProvider,
    private val sendNotification: SendNotification,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun invoke() = withContext(dispatcher) {
        val stock = getStock.invoke()
        for (product in stock.distinctBy { it.id }) {
            val lastKnownStock = getLastKnownStock.invoke(product.id)
            setLastKnownStock.invoke(product.id, product.amount)
            if (settingsRepository.isOrderNotificationsEnabled() && lastKnownStock > product.amount) {
                val message = createOrderMessage(
                    amountSold = lastKnownStock - product.amount,
                    productTitle = product.productTitle,
                    newStock = product.amount
                )
                sendNotification.invoke(product.productTitle, message)
            }

            if (settingsRepository.isBuyBoxNotificationsEnabled()) {
                val buyBoxStatus = getBuyBoxStatus.invoke(product.id)
                setBuyBoxStatus.invoke(product.id, product.hasBuyBox)
                if (buyBoxStatus != null && buyBoxStatus != product.hasBuyBox && product.amount > 0) {
                    val message = createBuyBoxMessage(
                        productTitle = product.productTitle,
                        hasBuyBox = product.hasBuyBox
                    )
                    sendNotification.invoke(product.productTitle, message)
                }
            }
        }
    }

    private suspend fun createOrderMessage(
        amountSold: Int,
        productTitle: String,
        newStock: Int
    ): String {
        return String.format(
            locale = Locale.getDefault(),
            format = messageProvider.getOrderMessage(),
            args = *arrayOf(amountSold, productTitle, newStock)
        )
    }

    private suspend fun createBuyBoxMessage(productTitle: String, hasBuyBox: Boolean): String {
        return String.format(
            locale = Locale.getDefault(),
            format = if (hasBuyBox) {
                messageProvider.getBuyBoxGainedMessage()
            } else {
                messageProvider.getBuyBoxLostMessage()
            },
            args = *arrayOf(productTitle)
        )
    }
}

