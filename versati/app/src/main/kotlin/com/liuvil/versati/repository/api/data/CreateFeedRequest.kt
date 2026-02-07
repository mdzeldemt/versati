package com.liuvil.versati.repository.api.data

import java.net.URL

data class CreateFeedRequest(
    val feedUrl: URL,
    val categoryId: Int
)