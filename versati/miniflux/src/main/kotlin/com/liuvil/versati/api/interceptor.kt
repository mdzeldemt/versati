package com.liuvil.versati.api

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Base64

class AuthenticationInterceptor(
    private val authentication: MinifluxAuthenticationMethod
): Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val headers = Headers.Builder()
            .apply {
                when (authentication) {
                    is MinifluxBasicAuthentication -> add(
                        "Authorization",
                        "Basic ${Base64.getEncoder().encodeToString("${authentication.username}:${authentication.password}".toByteArray())}"
                    )
                    is MinifluxApiKeyAuthentication -> add(
                        "X-Auth-Token", authentication.apiKey
                    )
                }
            }
            .build()

        val authorizedRequest =
            originalRequest.newBuilder()
                .headers(headers)
                .build()

        return chain.proceed(authorizedRequest)
    }
}