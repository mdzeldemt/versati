package com.mdzeldemt.versati.repository.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mdzeldemt.versati.repository.data.Icon

@Entity(tableName = "icons")
data class Icon(
    @PrimaryKey override val id: Int,
    override val data: String
): Icon
