package com.nielsmasdorp.lvbnotifier.data.auth

import com.nielsmasdorp.lvbnotifier.data.auth.response.TokenResponse
import retrofit2.http.*

interface AuthService {

    @FormUrlEncoded
    @POST("token")
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): TokenResponse
}