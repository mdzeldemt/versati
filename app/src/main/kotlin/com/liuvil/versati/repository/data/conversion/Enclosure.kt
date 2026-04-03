package com.liuvil.versati.repository.data.conversion

import com.liuvil.versati.repository.cache.data.Enclosure

fun com.liuvil.versati.repository.api.data.Enclosure.toCache(): Enclosure =
    Enclosure(
        id = id,
        url = url,
        mimeType = mimeType,
        size = size,
        entryId = entryId
    )