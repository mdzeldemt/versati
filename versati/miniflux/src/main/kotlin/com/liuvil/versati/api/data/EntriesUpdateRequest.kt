package com.liuvil.versati.api.data

data class EntriesUpdateRequest(
    val entryIds: List<Int>,
    val status: EntryStatus
)