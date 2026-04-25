package com.mdzeldemt.versati.repository.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mdzeldemt.versati.repository.cache.data.Enclosure

@Dao
interface EnclosureCache {
    @Query("SELECT * FROM enclosures WHERE id = :id")
    suspend fun getEnclosureById(id: Int): Enclosure?

    @Query("SELECT * FROM enclosures WHERE entryId = :entryId")
    suspend fun getEnclosuresByEntryId(entryId: Int): List<Enclosure>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnclosure(enclosure: Enclosure)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnclosures(enclosures: List<Enclosure>)
}