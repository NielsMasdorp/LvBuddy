package com.nielsmasdorp.lvbnotifier.presentation.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.TELEGRAM_BOT_TOKEN
import com.nielsmasdorp.domain.settings.SettingsRepository.Companion.TELEGRAM_CHAT_ID
import com.nielsmasdorp.lvbnotifier.R
import com.nielsmasdorp.lvbnotifier.util.SharedPrefsDataStore
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val preferenceDataStore: SharedPrefsDataStore by inject()

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = preferenceDataStore
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            showError(error.preferenceKeys, error.errorMessage)
        })
        viewModel.telegramSettingsVisible.observe(viewLifecycleOwner, Observer { showTelegramSettings ->
            findPreference<EditTextPreference>(TELEGRAM_CHAT_ID)?.isVisible = showTelegramSettings
            findPreference<EditTextPreference>(TELEGRAM_BOT_TOKEN)?.isVisible = showTelegramSettings
        })
        preferenceDataStore.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun showError(keys: List<String>, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        for (key in keys) {
            findPreference<SwitchPreferenceCompat>(key)!!.isChecked = false
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (preferenceDataStore.sharedPreferences.all[key]) {
            is String -> {
                viewModel.onStringValueChanged(
                    key,
                    preferenceDataStore.sharedPreferences.getString(key, "")!!
                )
            }
            is Boolean -> {
                viewModel.onNotificationSettingChanged(
                    key,
                    preferenceDataStore.sharedPreferences.getBoolean(key, false)
                )
            }
        }
    }
}