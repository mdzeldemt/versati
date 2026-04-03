package com.liuvil.versati.repository.data.conversion

import com.liuvil.versati.repository.cache.data.Category

fun com.liuvil.versati.repository.api.data.Category.toCache(): Category =
    Category(
        id = id,
        title = title
    )