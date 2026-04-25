package com.mdzeldemt.versati.repository.api.di

import com.mdzeldemt.versati.repository.api.ApiClient
import com.mdzeldemt.versati.repository.api.auth.AuthInterceptor
import com.mdzeldemt.versati.repository.api.converter.BodyConverterFactory
import com.mdzeldemt.versati.repository.api.converter.QueryParamConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL

@Module
@InstallIn(SingletonComponent::class)
class ApiClientModule {

    @Provides
    fun provideApiClient(
        baseUrl: URL,
        authInterceptor: AuthInterceptor
    ): ApiClient =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build()
            )
            .addConverterFactory(BodyConverterFactory.create())
            .addConverterFactory(QueryParamConverterFactory())
            .build()
            .create(ApiClient::class.java)
}