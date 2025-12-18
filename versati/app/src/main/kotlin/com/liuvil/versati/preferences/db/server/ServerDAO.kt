package com.liuvil.versati.preferences.db.server

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServerDAO {
    @Query("SELECT * FROM servers")
    suspend fun getAll(): List<Server>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getById(id: Int): Server?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(server: Server)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun delete(id: Int)
}