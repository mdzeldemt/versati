package com.liuvil.versati.repository.api.di

import com.liuvil.versati.repository.api.APIClient
import com.liuvil.versati.repository.api.auth.AuthInterceptor
import com.liuvil.versati.repository.api.converter.BodyConverterFactory
import com.liuvil.versati.repository.api.converter.QueryParamConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL

@Module
@InstallIn(SingletonComponent::class)
class APIClientModule {

    @Provides
    fun provideAPIClient(
        baseURL: URL,
        authInterceptor: AuthInterceptor
    ): APIClient =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build()
            )
            .addConverterFactory(BodyConverterFactory.Companion.create())
            .addConverterFactory(QueryParamConverterFactory())
            .build()
            .create(APIClient::class.java)
}