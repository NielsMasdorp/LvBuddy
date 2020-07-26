package com.nielsmasdorp.lvbnotifier.util

import android.content.SharedPreferences
import androidx.preference.PreferenceDataStore

/**
 * Wrapper around [PreferenceDataStore] which exposes its [SharedPreferences] because [PreferenceDataStore] doesn't
 */
abstract class SharedPrefsDataStore : PreferenceDataStore() {

    abstract val sharedPreferences: SharedPreferences
}