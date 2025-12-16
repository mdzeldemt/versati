package com.liuvil.versati.di

import com.liuvil.versati.repository.api.auth.Credentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import java.net.URL

@Module
@InstallIn(ActivityComponent::class, SingletonComponent::class)
class ApplicationModule {
    @Provides
    fun provideBaseURL(): URL =
        URL("")

    @Provides
    fun provideCredentials(): Credentials =
        Credentials.APIKey(
            apiKey = ""
        )
}