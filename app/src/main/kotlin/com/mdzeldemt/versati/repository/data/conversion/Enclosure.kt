package com.mdzeldemt.versati.repository.data.conversion

import com.mdzeldemt.versati.repository.cache.data.Enclosure

fun com.mdzeldemt.versati.repository.api.data.Enclosure.toCache(): Enclosure =
    Enclosure(
        id = id,
        url = url,
        mimeType = mimeType,
        size = size,
        entryId = entryId
    )