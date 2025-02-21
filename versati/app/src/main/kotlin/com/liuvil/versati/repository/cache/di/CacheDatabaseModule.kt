package com.liuvil.versati.repository.cache.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.liuvil.versati.repository.cache.CacheDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File

private const val CACHE_DATABASE_NAME = "network_cache_db"

@Module
@InstallIn(SingletonComponent::class)
class CacheDatabaseModule {
    @Provides
    fun provideCacheDatabase(
        @ApplicationContext context: Context
    ): CacheDatabase {
        val cacheDir = context.cacheDir
        val databaseFile = File(cacheDir, CACHE_DATABASE_NAME)
        return Room.databaseBuilder(context, CacheDatabase::class.java, databaseFile.absolutePath)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()
    }
}