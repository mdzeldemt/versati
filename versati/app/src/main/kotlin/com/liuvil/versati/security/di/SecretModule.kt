package com.liuvil.versati.security.di

import com.liuvil.versati.framework.random.nextString
import com.liuvil.versati.security.SecretStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import javax.inject.Qualifier

private const val PREFERENCE_SECRET_NAME = "preferences"
private const val SECRET_DEFAULT_LENGTH = 32

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class PreferenceSecret

@Module
@InstallIn(SingletonComponent::class)
class SecretModule {
    @Provides
    @PreferenceSecret
    fun providePreferenceSecret(
        secretStore: SecretStore
    ): String =
        secretStore.loadSecret(PREFERENCE_SECRET_NAME)
            ?: generateRandomSecret().also { secret ->
                secretStore.storeSecret(PREFERENCE_SECRET_NAME, secret)
            }
}

private fun generateRandomSecret(): String =
    SecureRandom().nextString(SECRET_DEFAULT_LENGTH)
