package com.nielsmasdorp.lvbnotifier.presentation.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nielsmasdorp.domain.clipboard.SaveProductEAN
import com.nielsmasdorp.domain.notifications.MessageProvider
import com.nielsmasdorp.domain.settings.CanShowStock
import com.nielsmasdorp.domain.stock.GetStock
import com.nielsmasdorp.domain.stock.Stock
import com.nielsmasdorp.lvbnotifier.util.Resource
import com.nielsmasdorp.lvbnotifier.util.Resource.Companion.loading
import com.nielsmasdorp.lvbnotifier.util.Resource.Companion.success
import com.nielsmasdorp.lvbnotifier.util.Resource.Companion.error
import com.nielsmasdorp.lvbnotifier.util.SingleLiveEvent
import com.nielsmasdorp.lvbnotifier.util.Status
import kotlinx.coroutines.*

class StockViewModel(
    private val canShowStock: CanShowStock,
    private val getStock: GetStock,
    private val saveProductEAN: SaveProductEAN,
    private val messageProvider: MessageProvider
) : ViewModel() {

    private var completeStock = listOf<StockViewData>()

    private val _data = MutableLiveData<Resource<List<StockViewData>>>()

    val data: LiveData<Resource<List<StockViewData>>>
        get() = _data

    val message = SingleLiveEvent<Message>()

    val navigation = SingleLiveEvent<Navigation>()

    var filter: String? = null
        set(value) {
            field = value
            filterData(value)
        }

    private fun filterData(filter: String?) {
        if (_data.value?.status != Status.SUCCESS) return

        _data.value = filter?.let {
            success(completeStock.filter {
                it.productTitle.contains(filter, ignoreCase = true)
            })
        } ?: success(completeStock)
    }

    init {
        fetchData()
    }

    fun onRefresh() {
        fetchData()
    }

    fun onSettingsClicked() {
        navigation.value = Navigation.Settings
    }

    fun onStockClicked(stockViewData: StockViewData) {
        message.value = Message.FullProductDescription(stockViewData.productTitle)
    }

    fun onStockLongPress(stockViewData: StockViewData) {
        saveProductEAN.invoke(stockViewData.productId)
        message.value = Message.EANCopied
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                if (!canShowStock.invoke()) {
                    _data.value = error(messageProvider.getCanShowStockError())
                } else {
                    _data.value = loading()
                    val stock = getStock.invoke()
                    completeStock = mapStock(stock)
                    _data.value = success(completeStock)
                }
            } catch (error: Throwable) {
                _data.value = error(messageProvider.getGenericStockError())
            }
        }
    }

    private fun mapStock(stocks: List<Stock>): List<StockViewData> {
        return stocks.map { stock ->
            StockViewData(
                stock.id,
                stock.productTitle,
                stock.amount.toString(),
                stock.hasBuyBox
            )
        }
    }

    sealed class Message {
        data class FullProductDescription(val description: String) : Message()
        object EANCopied : Message()
    }

    sealed class Navigation {
        object Settings : Navigation()
    }
}