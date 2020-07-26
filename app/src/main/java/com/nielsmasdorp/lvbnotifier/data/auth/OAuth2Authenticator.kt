package com.nielsmasdorp.lvbnotifier.data.auth

import com.nielsmasdorp.domain.auth.TokenManager
import com.nielsmasdorp.lvbnotifier.data.auth.OAuth2Interceptor.Companion.AUTHORIZATION_HEADER_NAME
import com.nielsmasdorp.lvbnotifier.data.auth.OAuth2Interceptor.Companion.AUTHORIZATION_HEADER_PREFIX
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class OAuth2Authenticator(private val tokenManager: TokenManager) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request()
        val sentAccessToken: String? = request.header(AUTHORIZATION_HEADER_NAME)?.replace(AUTHORIZATION_HEADER_PREFIX, "")
        synchronized(tokenManager) {
            val storedToken = runBlocking { tokenManager.getToken() }
            if (sentAccessToken == storedToken || storedToken.isEmpty()) {
                runBlocking {
                    tokenManager.refreshToken()
                }
            }
        }

        val newToken = runBlocking { tokenManager.getToken() }
        return request.newBuilder()
            .header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_PREFIX + newToken)
            .build()
    }
}