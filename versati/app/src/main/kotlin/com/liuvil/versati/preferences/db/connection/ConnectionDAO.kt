package com.liuvil.versati.preferences.db.connection

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConnectionDAO {
    @Query("SELECT * FROM connections")
    suspend fun getAll(): List<Connection>

    @Query("SELECT * FROM connections WHERE id = :id")
    suspend fun getById(id: Long): Connection

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(connection: Connection): Long

    @Query("DELETE FROM connections WHERE id = :id")
    suspend fun delete(id: Long)
}