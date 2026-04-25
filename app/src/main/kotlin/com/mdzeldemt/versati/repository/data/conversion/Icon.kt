package com.mdzeldemt.versati.repository.data.conversion

import com.mdzeldemt.versati.repository.cache.data.Icon

fun com.mdzeldemt.versati.repository.api.data.Icon.toCache(): Icon =
    Icon(
        id = id,
        data = data
    )