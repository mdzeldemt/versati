package com.liuvil.versati.preferences.db.credential

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CredentialDAO {
    @Query("SELECT * FROM credentials WHERE connectionID = :connectionID")
    suspend fun getByConnectionID(connectionID: Long): Credentials

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(credentials: Credentials)
}