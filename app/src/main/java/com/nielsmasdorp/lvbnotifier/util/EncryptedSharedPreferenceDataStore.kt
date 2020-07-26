package com.nielsmasdorp.lvbnotifier.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * [SharedPrefsDataStore] implementation which uses encrypted [SharedPreferences]
 */
class EncryptedSharedPreferenceDataStore(private val context: Context) : SharedPrefsDataStore() {

    override val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            getMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    override fun putStringSet(key: String, values: MutableSet<String>?) {
        sharedPreferences.edit { putStringSet(key, values) }
    }

    override fun getLong(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return sharedPreferences.getFloat(key, defValue)
    }

    override fun putFloat(key: String, value: Float) {
        sharedPreferences.edit { putFloat(key, value) }
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? {
        return sharedPreferences.getStringSet(key, defValues)
    }

    override fun getString(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    override fun putString(key: String, value: String?) {
        sharedPreferences.edit { putString(key, value) }
    }

    private fun getMasterKey(): MasterKey {
        return MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    companion object {
        private const val FILE_NAME = "shared_prefs"
    }
}