package com.mdzeldemt.versati.repository.api

import com.mdzeldemt.versati.preferences.Credentials
import com.mdzeldemt.versati.repository.api.auth.AuthInterceptor
import com.mdzeldemt.versati.repository.api.converter.BodyConverterFactory
import com.mdzeldemt.versati.repository.api.converter.QueryParamConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL
import javax.inject.Inject

class ApiClientFactory @Inject constructor() {
    fun create(
        baseUrl: URL,
        credentials: Credentials
    ): ApiClient =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        AuthInterceptor(credentials)
                    )
                    .build()
            )
            .addConverterFactory(BodyConverterFactory.create())
            .addConverterFactory(QueryParamConverterFactory())
            .build()
            .create(ApiClient::class.java)
}
