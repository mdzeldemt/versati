package com.liuvil.versati.api

import com.liuvil.versati.api.data.EntriesResponse
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.Feed
import com.liuvil.versati.api.data.SortDirection

interface MinifluxApi {

    suspend fun getEntries(
        direction: SortDirection? = null,
        limit: Int? = null
    ): EntriesResponse

    suspend fun getEntry(
        id: Int
    ): Entry

    suspend fun getFeeds(): List<Feed>

}