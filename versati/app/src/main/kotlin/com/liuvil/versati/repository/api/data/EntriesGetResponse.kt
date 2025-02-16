package com.liuvil.versati.repository.api.data

data class EntriesGetResponse(
    val total: Int,
    val entries: List<Entry>
)