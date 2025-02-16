package com.liuvil.versati.repository.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liuvil.versati.repository.data.Enclosure
import java.net.URL

@Entity(tableName = "enclosures")
data class Enclosure(
    @PrimaryKey override val id: Int,
    override val url: URL,
    override val mimeType: String,
    override val size: Int,
    override val entryId: Int
): Enclosure
