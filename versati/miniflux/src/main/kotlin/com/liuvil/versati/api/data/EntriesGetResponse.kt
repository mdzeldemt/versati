package com.liuvil.versati.api.data

data class EntriesGetResponse(
    val total: Int,
    val entries: List<Entry>
)