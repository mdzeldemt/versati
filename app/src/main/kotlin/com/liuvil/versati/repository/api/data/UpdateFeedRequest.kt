package com.liuvil.versati.repository.api.data

import java.net.URL

data class UpdateFeedRequest(
    val title: String,
    val feedUrl: URL,
    val categoryId: Int
)
