package com.liuvil.versati.repository.api.auth

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http2.Header
import java.io.IOException
import java.util.Base64
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authContext: AuthContext
): Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val credentials = authContext.getCredentials()
            ?: return chain.proceed(originalRequest)

        val headers = originalRequest.headers.newBuilder()
            .apply {
                when (credentials) {
                    is Credentials.Basic -> add(
                        "Authorization",
                        "Basic ${
                            Base64.getEncoder()
                                .encodeToString(
                                    "${credentials.username}:${credentials.password}"
                                        .toByteArray()
                                )
                        }"
                    )

                    is Credentials.APIKey -> add(
                        "X-Auth-Token", credentials.apiKey
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
