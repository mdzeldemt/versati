package com.liuvil.versati.api

import com.liuvil.versati.api.converter.BodyConverterFactory
import com.liuvil.versati.api.converter.QueryParamConverterFactory
import com.liuvil.versati.api.retrofit.MinifluxRetrofitAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL

class MinifluxAPIFactory {
    fun create(
        baseURL: URL,
        httpClient: OkHttpClient
    ): MinifluxAPI =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(BodyConverterFactory.create())
            .addConverterFactory(QueryParamConverterFactory())
            .build()
            .create(MinifluxRetrofitAPI::class.java)
}
