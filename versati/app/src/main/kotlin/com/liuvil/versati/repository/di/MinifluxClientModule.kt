package com.liuvil.versati.repository.di

import com.liuvil.versati.repository.api.MinifluxClient
import com.liuvil.versati.repository.api.converter.BodyConverterFactory
import com.liuvil.versati.repository.api.converter.QueryParamConverterFactory
import com.liuvil.versati.repository.api.interceptor.MinifluxAuthenticationInterceptor
import com.liuvil.versati.repository.api.interceptor.MinifluxAuthenticationMethod
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL

@Module
@InstallIn(SingletonComponent::class)
class MinifluxClientModule {
    @Provides
    fun provideMinifluxClient(
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