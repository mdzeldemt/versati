package com.mdzeldemt.versati.repository.data.conversion

import com.mdzeldemt.versati.repository.cache.data.Feed

fun com.mdzeldemt.versati.repository.api.data.Feed.toCache(): Feed =
    Feed(
        id = id,
        feedUrl = feedUrl,
        siteUrl = siteUrl,
        title = title,
        description = description,
        checkedAt = checkedAt,
        nextCheckAt = nextCheckAt,
        categoryId = category.id,
        iconId = icon.iconId,
        hideGlobally = hideGlobally,
        parsingErrorCount = parsingErrorCount,
        parsingErrorMessage = parsingErrorMessage
    )
