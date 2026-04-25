package com.mdzeldemt.versati.repository.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mdzeldemt.versati.repository.cache.data.Icon

@Dao
interface IconCache {
    @Query("SELECT * FROM icons WHERE id = :id")
    suspend fun getIconById(id: Int): Icon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcon(icon: Icon)
}