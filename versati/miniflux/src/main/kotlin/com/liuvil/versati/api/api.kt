package com.liuvil.versati.api

import com.liuvil.versati.api.data.Category
import com.liuvil.versati.api.data.Enclosure
import com.liuvil.versati.api.data.EntriesGetResponse
import com.liuvil.versati.api.data.EntriesUpdateRequest
import com.liuvil.versati.api.data.Entry
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.api.data.Feed
import com.liuvil.versati.api.data.FeedCountersResponse
import com.liuvil.versati.api.data.Icon
import com.liuvil.versati.api.data.SortDirection

interface MinifluxApi {

    suspend fun getCategories(): List<Category>

    suspend fun getCategoryEntries(
        categoryId: Int,
        status: EntryStatus? = null,
        direction: SortDirection? = null,
        offset: Int? = null,
        beforeEntryId: Int? = null,
        afterEntryId: Int? = null,
        limit: Int? = null
    ): EntriesGetResponse

    suspend fun getEnclosure(
        id: Int
    ): Enclosure

    suspend fun getEntries(
        status: EntryStatus? = null,
        direction: SortDirection? = null,
        starred: Boolean? = null,
        offset: Int? = null,
        beforeEntryId: Int? = null,
        afterEntryId: Int? = null,
        globallyVisible: Boolean? = null,
        search: String? = null,
        limit: Int? = null
    ): EntriesGetResponse

    suspend fun getEntry(
        id: Int
    ): Entry

    suspend fun getFeedCounters(): FeedCountersResponse

    suspend fun getFeedEntries(
        feedId: Int,
        status: EntryStatus? = null,
        direction: SortDirection? = null,
        offset: Int? = null,
        beforeEntryId: Int? = null,
        afterEntryId: Int? = null,
        limit: Int? = null
    ): EntriesGetResponse

    suspend fun getFeedIcon(
        id: Int
    ): Icon

    suspend fun getFeedIconByFeedId(
        feedId: Int
    ): Icon

    suspend fun getFeeds(): List<Feed>

    suspend fun updateEntries(
        request: EntriesUpdateRequest
    )

}