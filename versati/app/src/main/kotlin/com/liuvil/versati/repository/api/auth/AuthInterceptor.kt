package com.liuvil.versati.repository.api.auth

import com.liuvil.versati.preferences.data.APIKeyCredential
import com.liuvil.versati.preferences.data.BasicCredential
import com.liuvil.versati.preferences.data.Credential
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Base64
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val credential: Credential
): Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val headers = originalRequest.headers.newBuilder()
            .apply {
                when (credential) {
                    is BasicCredential -> add(
                        "Authorization",
                        "Basic ${
                            Base64.getEncoder()
                                .encodeToString(
                                    "${credential.username}:${credential.password}"
                                        .toByteArray()
                                )
                        }"
                    )

                    is APIKeyCredential -> add(
                        "X-Auth-Token", credential.apiKey
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
