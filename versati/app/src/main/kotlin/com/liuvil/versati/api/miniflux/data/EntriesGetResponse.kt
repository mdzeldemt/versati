package com.liuvil.versati.api.miniflux.data

data class EntriesGetResponse(
    val total: Int,
    val entries: List<Entry>
)