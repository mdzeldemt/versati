package com.liuvil.versati.api.data

import java.net.URL
import java.time.OffsetDateTime

data class Feed(
    val id: Int,
    val userId: Int,
    val feedUrl: URL,
    val siteUrl: URL,
    val title: String,
    val description: String,
    val checkedAt: OffsetDateTime,
    val nextCheckAt: OffsetDateTime,
    val category: Category,
    val icon: Icon
)
