package com.nielsmasdorp.domain.auth

interface TokenManager {

    suspend fun refreshToken()

    suspend fun storeToken(token: String)

    suspend fun getToken(): String
}