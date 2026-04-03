package com.liuvil.versati.repository.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liuvil.versati.repository.data.Entry
import java.net.URL
import java.time.OffsetDateTime

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey override val id: Int,
    override val read: Boolean,
    override val title: String,
    override val url: URL,
    override val publishedAt: OffsetDateTime,
    override val createdAt: OffsetDateTime,
    override val changedAt: OffsetDateTime,
    override val content: String,
    override val author: String,
    override val starred: Boolean,
    override val feedId: Int
): Entry
