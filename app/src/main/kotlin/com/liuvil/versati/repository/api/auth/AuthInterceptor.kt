package com.liuvil.versati.repository.api.auth

import com.liuvil.versati.preferences.ApiKeyCredentials
import com.liuvil.versati.preferences.BasicCredentials
import com.liuvil.versati.preferences.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Base64
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val credentials: Credentials
): Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val headers = originalRequest.headers.newBuilder()
            .apply {
                when (credentials) {
                    is BasicCredentials -> add(
                        "Authorization",
                        "Basic ${
                            Base64.getEncoder()
                                .encodeToString(
                                    "${credentials.username}:${credentials.password}"
                                        .toByteArray()
                                )
                        }"
                    )

                    is ApiKeyCredentials -> add(
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
