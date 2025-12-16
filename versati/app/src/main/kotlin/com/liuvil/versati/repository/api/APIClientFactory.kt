package com.liuvil.versati.repository.api

import com.liuvil.versati.repository.api.converter.BodyConverterFactory
import com.liuvil.versati.repository.api.converter.QueryParamConverterFactory
import com.liuvil.versati.repository.api.auth.AuthInterceptor
import com.liuvil.versati.repository.api.auth.AuthContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL
import javax.inject.Inject

class APIClientFactory @Inject constructor(
    private val credentialsStore: AuthContext
) {
    fun create(
        baseURL: URL
    ): APIClient =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        AuthInterceptor(credentialsStore)
                    )
                    .build()
            )
            .addConverterFactory(BodyConverterFactory.create())
            .addConverterFactory(QueryParamConverterFactory())
            .build()
            .create(APIClient::class.java)
}
