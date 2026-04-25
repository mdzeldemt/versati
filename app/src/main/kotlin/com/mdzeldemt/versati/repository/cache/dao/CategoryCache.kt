package com.mdzeldemt.versati.repository.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mdzeldemt.versati.repository.cache.data.Category

@Dao
interface CategoryCache {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categories: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Int)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}