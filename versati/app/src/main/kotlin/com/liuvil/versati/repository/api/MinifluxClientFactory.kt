package com.liuvil.versati.repository.api

import com.liuvil.versati.repository.api.converter.BodyConverterFactory
import com.liuvil.versati.repository.api.converter.QueryParamConverterFactory
import com.liuvil.versati.repository.api.interceptor.MinifluxAuthenticationInterceptor
import com.liuvil.versati.repository.api.interceptor.MinifluxAuthenticationMethod
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL

class MinifluxClientFactory {
    fun create(
        baseURL: URL,
        authenticationMethod: MinifluxAuthenticationMethod
    ): MinifluxClient =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        MinifluxAuthenticationInterceptor(authenticationMethod)
                    )
                    .build()
            )
            .addConverterFactory(BodyConverterFactory.create())
            .addConverterFactory(QueryParamConverterFactory())
            .build()
            .create(MinifluxClient::class.java)
}
