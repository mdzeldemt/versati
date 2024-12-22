package com.liuvil.versati.api.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.liuvil.versati.api.AuthenticationInterceptor
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.MinifluxAuthenticationMethod
import com.liuvil.versati.api.MinifluxRetrofitApi
import com.liuvil.versati.api.serialization.EnumConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MinifluxBaseURL

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MinifluxAuthentication

@Module
@InstallIn(SingletonComponent::class)
object MinifluxModule {

    @Provides
    fun provideMinifluxApi(
        httpClient: OkHttpClient,
        @MinifluxBaseURL baseURL: URL
    ): MinifluxApi =
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseURL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .registerTypeAdapter(
                            OffsetDateTime::class.java,
                            JsonDeserializer { json: JsonElement, type: Type?, context: JsonDeserializationContext? ->
                                OffsetDateTime.parse(
                                    json.asString
                                )
                            } as JsonDeserializer<OffsetDateTime>
                        )
                        .create()
                )
            )
            .addConverterFactory(EnumConverterFactory())
            .build()
            .create(MinifluxRetrofitApi::class.java)

    @Provides
    fun provideHttpClient(
        @MinifluxAuthentication authentication: MinifluxAuthenticationMethod
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor(authentication))
            .build()
}
