package com.liuvil.versati.repository.data.conversion

import com.liuvil.versati.repository.cache.data.Feed

fun com.liuvil.versati.repository.api.data.Feed.toCache(): Feed =
    Feed(
        id = id,
        feedUrl = feedUrl,
        siteUrl = siteUrl,
        title = title,
        description = description,
        categoryId = category.id,
        iconId = icon.iconId,
        hideGlobally = hideGlobally
    )
