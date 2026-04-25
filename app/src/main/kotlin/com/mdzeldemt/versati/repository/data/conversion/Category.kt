package com.mdzeldemt.versati.repository.data.conversion

import com.mdzeldemt.versati.repository.cache.data.Category

fun com.mdzeldemt.versati.repository.api.data.Category.toCache(): Category =
    Category(
        id = id,
        title = title
    )