package com.liuvil.versati.repository.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liuvil.versati.repository.data.Feed
import java.net.URL
import java.time.OffsetDateTime

@Entity(tableName = "feeds")
data class Feed(
    @PrimaryKey override val id: Int,
    override val feedUrl: URL,
    override val siteUrl: URL,
    override val title: String,
    override val description: String,
    override val checkedAt: OffsetDateTime,
    override val nextCheckAt: OffsetDateTime,
    override val categoryId: Int,
    override val hideGlobally: Boolean,
    override val iconId: Int,
    override val parsingErrorCount: Int,
    override val parsingErrorMessage: String
): Feed