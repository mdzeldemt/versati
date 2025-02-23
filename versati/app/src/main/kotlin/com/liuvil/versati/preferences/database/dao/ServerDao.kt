package com.liuvil.versati.preferences.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.liuvil.versati.preferences.database.data.Server

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers")
    suspend fun getAllServers(): List<Server>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerById(id: Int): Server?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: Server)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun deleteServerById(id: Int)
}