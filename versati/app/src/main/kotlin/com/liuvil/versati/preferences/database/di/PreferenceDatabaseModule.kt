package com.liuvil.versati.preferences.database.di

import android.content.Context
import androidx.room.Room
import com.liuvil.versati.preferences.database.PreferenceDatabase
import com.liuvil.versati.security.di.PreferenceSecret
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory

private const val PREFERENCE_DATABASE_NAME = "preferences"

@Module
@InstallIn(SingletonComponent::class)
class PreferenceDatabaseModule {
    @Provides
    fun providePreferenceDatabase(
        @ApplicationContext context: Context,
        @PreferenceSecret secret: String
    ): PreferenceDatabase {
        val factory = SupportFactory(secret.toByteArray())
        return Room.databaseBuilder(context, PreferenceDatabase::class.java, PREFERENCE_DATABASE_NAME)
            .openHelperFactory(factory)
            .build()
    }
}