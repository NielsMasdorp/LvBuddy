package com.nielsmasdorp.lvbnotifier.data.auth

import com.nielsmasdorp.domain.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class OAuth2Interceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { if (tokenManager.getToken().isBlank()) INVALID_TOKEN else tokenManager.getToken() }
        return chain.proceed(chain.request().newBuilder().also { builder ->
            builder.header(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_PREFIX + token)
        }.build())
    }

    companion object {

        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        const val AUTHORIZATION_HEADER_PREFIX = "Bearer "

        // Since the API will not respond with 401 but with 400
        // (thus not triggering the [OAuth2Authenticator] when no token is supplied,
        // we need to send an invalid token to start */
        const val INVALID_TOKEN = "eyJraWQiOiJyc2EyIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJkMjJiMGQyZS02ZTI2LTQ2YTItYWM1Ni1hZmM1ZDg4YjIzMWYiLCJhenAiOiJkMjJiMGQyZS02ZTI2LTQ2YTItYWM1Ni1hZmM1ZDg4YjIzMWYiLCJjbGllbnRuYW1lIjoiTFZCIiwiaXNzIjoiaHR0cHM6XC9cL2xvZ2luLmJvbC5jb20iLCJzY29wZXMiOiJSRVRBSUxFUiIsImV4cCI6MTU3ODQwMjMxOSwiaWF0IjoxNTc4NDAyMDE5LCJhaWQiOiJDTE5UQzpiZTJiNzAxNi03MzQ2LWMzNmUtZDEzOC03NzcwODE3M2Y3YmMgU0xSOjE0NTk0NDIiLCJqdGkiOiJmODIzMTQ4OC1kNGIyLTQ1NGQtODkyMS02YTczMzQzYTE3NDcifQ.XDi4fSGHDE9vr4oD8TsRD_AfQFLQBalpmSJK1pYPj--cSPoMBVnr0ijXwu7AmWTHdw9kF4kD9k_QjGb2tQuadteakhkpbx_cICPDoLNyHagyQX47n4uARQjj5VWKjxeGP27Z63uiM28QAYDH1fwaRKFDsfN7BpoNsUX2PRfrJqo3W2rwJptMkDAYfAj1xlMJiRCyOgnJFV-3eYQUzLrmDjAJ07agRmr0Vl1hJxQSDJUGvQoWZeh9ObvPECAqozr0XPQNrUNivnWOozlipe8HqWBd3frQUjTooil75qm2_BsKgoxBTWl4IYQtO4tr6HJ1vZpUKX-P-28x6QvsG-GQ6w"
    }
}