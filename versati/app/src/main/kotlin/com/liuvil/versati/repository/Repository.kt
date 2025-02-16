package com.liuvil.versati.repository

import com.liuvil.versati.repository.api.MinifluxClient
import com.liuvil.versati.repository.api.data.EntriesGetResponse
import com.liuvil.versati.repository.api.data.EntriesUpdateRequest
import com.liuvil.versati.repository.api.data.EntryStatus
import com.liuvil.versati.repository.api.data.FeedCountersResponse
import com.liuvil.versati.repository.api.data.SortDirection
import com.liuvil.versati.repository.cache.database.CacheDatabase
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Enclosure
import com.liuvil.versati.repository.data.Entry
import com.liuvil.versati.repository.data.Feed
import com.liuvil.versati.repository.data.Icon
import com.liuvil.versati.repository.data.conversion.toCache
import javax.inject.Inject

class Repository @Inject constructor(
    private val minifluxClient: MinifluxClient,
    cacheDatabase: CacheDatabase
) {
    private val categoryCache = cacheDatabase.categoryCache()
    private val enclosureCache = cacheDatabase.enclosureCache()
    private val entryCache = cacheDatabase.entryCache()
    private val feedCache = cacheDatabase.feedCache()
    private val iconCache = cacheDatabase.iconCache()

    // Categories
    suspend fun getAllCategories(
        origin: Origin = Origin.Remote
    ): List<Category> = ResourceProvider(
        local = {
            categoryCache.getAllCategories()
        },
        remote = {
            minifluxClient.getCategories()
                .map {
                    it.toCache()
                }
        },
        sync = {
            categoryCache.deleteAllCategories()
            categoryCache.insertCategories(it)
        }
    ).provide(origin)

    // Enclosures
    suspend fun getEnclosuresByEntryId(
        entryId: Int,
        origin: Origin = Origin.Remote
    ): List<Enclosure> = ResourceProvider(
        local = {
            enclosureCache.getEnclosuresByEntryId(entryId)
        },
        remote = {
            minifluxClient.getEntry(entryId).enclosures
                .map {
                    it.toCache()
                }
        },
        sync = {
            enclosureCache.insertEnclosures(it)
        }
    ).provide(origin)

    // Entries
    suspend fun getEntryById(
        id: Int,
        origin: Origin = Origin.Remote
    ): Entry =
        ResourceProvider(
            local = {
                entryCache.getEntryById(id)
            },
            remote = {
                minifluxClient.getEntry(id).toCache()
            },
            sync = {
                entryCache.insertEntry(it)
            }
        ).provide(origin)

    suspend fun getAllEntries(
        read: Boolean? = null,
        starred: Boolean? = null,
        globallyVisible: Boolean? = null,
        search: String? = null,
        offset: Int? = null,
        limit: Int? = null
    ): EntriesGetResponse =
        minifluxClient.getEntries(
            status = read?.let {
                if (it) EntryStatus.READ else EntryStatus.UNREAD
            },
            direction = SortDirection.DESCENDING,
            starred = starred,
            offset = offset,
            globallyVisible = globallyVisible,
            search = search,
            limit = limit
        ).also { entriesResponse ->
            entryCache.insertEntries(
                entriesResponse.entries.map { it.toCache() }
            )
        }

    suspend fun getEntriesFromCategory(
        categoryId: Int,
        read: Boolean? = null,
        offset: Int? = null,
        limit: Int? = null
    ): EntriesGetResponse =
        minifluxClient.getCategoryEntries(
            categoryId = categoryId,
            status = read?.let {
                if (it) EntryStatus.READ else EntryStatus.UNREAD
            },
            direction = SortDirection.DESCENDING,
            offset = offset,
            limit = limit
        ).also { entriesResponse ->
            entryCache.insertEntries(
                entriesResponse.entries.map { it.toCache() }
            )
        }

    suspend fun getEntriesFromFeed(
        feedId: Int,
        read: Boolean? = null,
        offset: Int? = null,
        limit: Int? = null
    ): EntriesGetResponse =
        minifluxClient.getFeedEntries(
            feedId = feedId,
            status = read?.let {
                if (it) EntryStatus.READ else EntryStatus.UNREAD
            },
            direction = SortDirection.DESCENDING,
            offset = offset,
            limit = limit
        ).also { entriesResponse ->
            entryCache.insertEntries(
                entriesResponse.entries.map { it.toCache() }
            )
        }

    suspend fun updateEntriesRead(
        ids: List<Int>,
        read: Boolean
    ) {
        minifluxClient.updateEntries(
            EntriesUpdateRequest(
                entryIds = ids,
                status = if (read) EntryStatus.READ else EntryStatus.UNREAD
            )
        )
        entryCache.updateEntriesRead(ids, read)
    }

    suspend fun toggleEntryStarred(id: Int) {
        minifluxClient.toggleEntryBookmark(id)
        entryCache.getEntryById(id)?.let {
            entryCache.updateEntryStarred(id, !it.starred)
        }
    }

    // Feeds
    suspend fun getFeedById(
        id: Int,
        origin: Origin = Origin.Remote
    ): Feed = ResourceProvider(
        local = {
            feedCache.getFeedById(id)
        },
        remote = {
            minifluxClient.getFeed(id).toCache()
        },
        sync = {
            feedCache.insertFeed(it)
        }
    ).provide(origin)

    suspend fun getAllFeeds(
        origin: Origin = Origin.Remote
    ): List<Feed> = ResourceProvider(
        local = {
            feedCache.getAllFeeds()
        },
        remote = {
            minifluxClient.getFeeds()
                .map { it.toCache() }
        },
        sync = {
            feedCache.deleteAllFeeds()
            feedCache.insertFeeds(it)
        }
    ).provide(origin)

    suspend fun getFeedCounters(): FeedCountersResponse =
        minifluxClient.getFeedCounters()

    // Icons
    suspend fun getIconById(
        id: Int,
        origin: Origin = Origin.Remote
    ): Icon = ResourceProvider(
        local = {
            iconCache.getIconById(id)
        },
        remote = {
            minifluxClient.getFeedIcon(id).toCache()
        },
        sync = {
            iconCache.insertIcon(it)
        }
    ).provide(origin)
}
