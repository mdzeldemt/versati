package com.mdzeldemt.versati.repository.api.data

import java.net.URL
import java.time.OffsetDateTime

data class Entry(
    val id: Int,
    val userId: Int,
    val feedId: Int,
    val status: EntryStatus,
    val hash: String,
    val title: String,
    val url: URL,
    val publishedAt: OffsetDateTime,
    val createdAt: OffsetDateTime,
    val changedAt: OffsetDateTime,
    val content: String,
    val author: String,
    val shareCode: String,
    val starred: Boolean,
    val readingTime: Int,
    val enclosures: List<Enclosure>,
    val feed: Feed,
    val tags: List<String>?
)
