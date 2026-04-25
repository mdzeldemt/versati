package com.mdzeldemt.versati.repository.data.conversion

import com.mdzeldemt.versati.repository.api.data.EntryStatus
import com.mdzeldemt.versati.repository.cache.data.Entry

fun com.mdzeldemt.versati.repository.api.data.Entry.toCache(): Entry =
    Entry(
        id = id,
        read = status == EntryStatus.READ,
        title = title,
        url = url,
        publishedAt = publishedAt,
        createdAt = createdAt,
        changedAt = changedAt,
        content = content,
        author = author,
        starred = starred,
        feedId = feedId,
    )
