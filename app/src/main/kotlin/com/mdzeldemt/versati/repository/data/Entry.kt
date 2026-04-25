package com.mdzeldemt.versati.repository.data

import java.net.URL
import java.time.OffsetDateTime

interface Entry {
    val id: Int
    val read: Boolean
    val title: String
    val url: URL
    val publishedAt: OffsetDateTime
    val createdAt: OffsetDateTime
    val changedAt: OffsetDateTime
    val content: String
    val author: String
    val starred: Boolean
    val feedId: Int
}
