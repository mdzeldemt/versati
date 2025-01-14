package com.liuvil.versati.api.miniflux

import com.liuvil.versati.api.miniflux.data.Category
import com.liuvil.versati.api.miniflux.data.Enclosure
import com.liuvil.versati.api.miniflux.data.EntriesGetResponse
import com.liuvil.versati.api.miniflux.data.EntriesUpdateRequest
import com.liuvil.versati.api.miniflux.data.Entry
import com.liuvil.versati.api.miniflux.data.EntryStatus
import com.liuvil.versati.api.miniflux.data.Feed
import com.liuvil.versati.api.miniflux.data.FeedCountersResponse
import com.liuvil.versati.api.miniflux.data.Icon
import com.liuvil.versati.api.miniflux.data.SortDirection

interface MinifluxAPI {

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

    suspend fun toggleEntryBookmark(
        id: Int
    )

    suspend fun updateEntries(
        request: EntriesUpdateRequest
    )

}