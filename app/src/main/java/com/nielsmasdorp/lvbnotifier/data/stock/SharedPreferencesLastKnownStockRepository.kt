package com.nielsmasdorp.lvbnotifier.data.stock

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.nielsmasdorp.domain.stock.LastKnownStockRepository

class SharedPreferencesLastKnownStockRepository(private val context: Context) :
    LastKnownStockRepository {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override suspend fun getLastKnownStock(productId: String): Int {
        return preferences.getInt(productId, -1)
    }

    override suspend fun saveLastKnownStock(productId: String, stock: Int) {
        preferences.edit { putInt(productId, stock) }
    }

    override suspend fun getBuyBoxStatus(productId: String): Boolean? {
        val stringVal = preferences.getString(BUY_BOX_PREFIX + productId, null)
        return stringVal?.toBoolean()
    }

    override suspend fun saveBuyBoxStatus(productId: String, hasBuyBox: Boolean) {
        preferences.edit { putString(BUY_BOX_PREFIX + productId, hasBuyBox.toString()) }
    }

    companion object {
        private const val BUY_BOX_PREFIX = "buybox_"
    }
}