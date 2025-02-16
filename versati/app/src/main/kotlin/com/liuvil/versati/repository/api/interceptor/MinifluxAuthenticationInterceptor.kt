package com.liuvil.versati.repository.api.interceptor

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Base64

sealed interface MinifluxAuthenticationMethod {
    data class Basic(
        val username: String,
        val password: String
    ): MinifluxAuthenticationMethod

    data class APIKey(
        val apiKey: String
    ): MinifluxAuthenticationMethod
}

class MinifluxAuthenticationInterceptor(
    private val authenticationMethod: MinifluxAuthenticationMethod
): Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val headers = Headers.Builder()
            .apply {
                when (authenticationMethod) {
                    is MinifluxAuthenticationMethod.Basic -> add(
                        "Authorization",
                        "Basic ${Base64.getEncoder().encodeToString("${authenticationMethod.username}:${authenticationMethod.password}".toByteArray())}"
                    )
                    is MinifluxAuthenticationMethod.APIKey -> add(
                        "X-Auth-Token", authenticationMethod.apiKey
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
