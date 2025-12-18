package com.liuvil.versati.preferences.db.di

import android.content.Context
import androidx.room.Room
import com.liuvil.versati.preferences.db.PreferenceDatabase
import com.liuvil.versati.preferences.db.credential.CredentialDAO
import com.liuvil.versati.preferences.db.server.ServerDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

private const val PREFERENCES_DATABASE_NAME = "preferences"

@Module
@InstallIn(SingletonComponent::class)
class PreferencesDatabaseModule {
    @Provides
    fun providePreferencesDatabase(
        @ApplicationContext context: Context
    ): PreferenceDatabase {
        return Room
            .databaseBuilder(
                context,
                PreferenceDatabase::class.java,
                PREFERENCES_DATABASE_NAME
            )
            .build()
    }

    @Provides
    fun provideCredentialDao(
        database: PreferenceDatabase
    ): CredentialDAO = database.credentialDAO()

    @Provides
    fun provideServerDao(
        database: PreferenceDatabase
    ): ServerDAO = database.serverDAO()
}