package com.nielsmasdorp.lvbnotifier.data.network

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Interceptor which sets cache headers on requests which then get respected by OkHttp
 */
class CacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val cacheControl = CacheControl.Builder()
            .maxAge(CACHE_MAX_AGE_MINUTES, TimeUnit.MINUTES)
            .build()

        return response.newBuilder()
            .removeHeader(PRAGMA_HEADER)
            .removeHeader(CACHE_CONTROL_HEADER)
            .header(CACHE_CONTROL_HEADER, cacheControl.toString())
            .build()
    }

    companion object {
        private const val PRAGMA_HEADER = "Pragma"
        private const val CACHE_CONTROL_HEADER = "Cache-Control"
        private const val CACHE_MAX_AGE_MINUTES = 1
    }
}