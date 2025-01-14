package com.liuvil.versati.api.miniflux.data

data class EntriesUpdateRequest(
    val entryIds: List<Int>,
    val status: EntryStatus
)