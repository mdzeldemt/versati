package com.mdzeldemt.versati.repository.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mdzeldemt.versati.repository.data.Category

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey override val id: Int,
    override val title: String
): Category
