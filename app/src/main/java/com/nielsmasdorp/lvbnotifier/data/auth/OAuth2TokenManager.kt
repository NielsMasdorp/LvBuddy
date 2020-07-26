package com.nielsmasdorp.lvbnotifier.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.nielsmasdorp.domain.auth.TokenManager
import com.nielsmasdorp.domain.settings.SettingsRepository

class OAuth2TokenManager(
    private val context: Context,
    private val authService: AuthService,
    private val settingsRepository: SettingsRepository
) : TokenManager {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override suspend fun refreshToken() {
        val token = authService.getToken(
            clientId = settingsRepository.getRetailerApiClientId(),
            clientSecret = settingsRepository.getRetailerApiClientSecret()
        ).token
        storeToken(token)
    }

    override suspend fun storeToken(token: String) {
        preferences.edit().putString(getTokenIdentifier(), token).apply()
    }

    override suspend fun getToken(): String {
        return preferences.getString(getTokenIdentifier(), "")!!
    }

    private suspend fun getTokenIdentifier(): String {
        return "${settingsRepository.getRetailerApiClientId()}$AUTH_TOKEN_KEY"
    }

    companion object {
        private const val AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY"
    }
}