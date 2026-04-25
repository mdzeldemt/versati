package com.mdzeldemt.versati.repository.cache.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mdzeldemt.versati.repository.cache.CacheDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

private const val CACHE_DATABASE_NAME = "network_cache_db"

@Module
@InstallIn(SingletonComponent::class)
object CacheDatabaseModule {
    @Provides
    @Singleton
    fun provideCacheDatabase(
        @ApplicationContext context: Context
    ): CacheDatabase {
        val cacheDir = context.cacheDir
        val databaseFile = File(cacheDir, CACHE_DATABASE_NAME)
        return Room.databaseBuilder(context, CacheDatabase::class.java, databaseFile.absolutePath)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .fallbackToDestructiveMigration()
            .build()
    }
}