package com.liuvil.versati.repository.data.conversion

import com.liuvil.versati.repository.api.data.EntryStatus
import com.liuvil.versati.repository.cache.data.Entry

fun com.liuvil.versati.repository.api.data.Entry.toCache(): Entry =
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
