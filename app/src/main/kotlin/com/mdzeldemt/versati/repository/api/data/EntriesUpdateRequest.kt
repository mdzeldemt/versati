package com.mdzeldemt.versati.repository.api.data

data class EntriesUpdateRequest(
    val entryIds: List<Int>,
    val status: EntryStatus
)