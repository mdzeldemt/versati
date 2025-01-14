package com.liuvil.versati.di

import com.liuvil.versati.api.miniflux.MinifluxAPI
import com.liuvil.versati.api.miniflux.MinifluxAPIFactory
import com.liuvil.versati.api.miniflux.interceptor.MinifluxAuthenticationInterceptor
import com.liuvil.versati.api.miniflux.interceptor.MinifluxAuthenticationMethod
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.net.URL

@Module
@InstallIn(ActivityComponent::class, SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideMinifluxAPI(): MinifluxAPI =
        MinifluxAPIFactory()
            .create(
                baseURL = URL(""),
                httpClient = OkHttpClient.Builder()
                    .addInterceptor(
                        MinifluxAuthenticationInterceptor(
                            MinifluxAuthenticationMethod.APIKey(
                                apiKey = ""
                            )
                        )
                    )
                    .build()
            )

}