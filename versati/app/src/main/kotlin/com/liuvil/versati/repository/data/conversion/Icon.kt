package com.liuvil.versati.repository.data.conversion

import com.liuvil.versati.repository.cache.data.Icon

fun com.liuvil.versati.repository.api.data.Icon.toCache(): Icon =
    Icon(
        id = id,
        data = data
    )