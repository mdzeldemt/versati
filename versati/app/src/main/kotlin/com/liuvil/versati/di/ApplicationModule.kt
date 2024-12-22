package com.liuvil.versati.di

import com.liuvil.versati.api.MinifluxAuthenticationMethod
import com.liuvil.versati.api.MinifluxBasicAuthentication
import com.liuvil.versati.api.di.MinifluxAuthentication
import com.liuvil.versati.api.di.MinifluxBaseURL
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
    @MinifluxBaseURL
    fun provideMinifluxBaseURL(): URL =
        URL("")

    @Provides
    @MinifluxAuthentication
    fun provideMinifluxAuthentication(): MinifluxAuthenticationMethod =
        MinifluxBasicAuthentication("", "")

}