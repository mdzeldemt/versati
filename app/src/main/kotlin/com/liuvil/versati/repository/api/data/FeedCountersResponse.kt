package com.liuvil.versati.repository.api.data

data class FeedCountersResponse(
    val reads: Map<Int, Int>,
    val unreads: Map<Int, Int>
)
