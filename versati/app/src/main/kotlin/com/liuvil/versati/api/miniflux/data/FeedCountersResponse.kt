package com.liuvil.versati.api.miniflux.data

data class FeedCountersResponse(
    val reads: Map<Int, Int>,
    val unreads: Map<Int, Int>
)
