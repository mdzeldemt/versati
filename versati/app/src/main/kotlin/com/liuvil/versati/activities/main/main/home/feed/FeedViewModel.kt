package com.liuvil.versati.activities.main.main.home.feed

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.api.data.EntriesGetResponse
import com.liuvil.versati.repository.api.data.FeedCountersResponse
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Enclosure
import com.liuvil.versati.repository.data.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<Unit>() {

    private lateinit var repository: Repository

    private val _categories = mutableStateOf<LazyResult<List<Category>>>(None())
    private val _feeds = mutableStateOf<LazyResult<List<Feed>>>(None())
    private val _feedCounters = mutableStateOf<LazyResult<FeedCountersResponse>>(None())
    private val _iconsById = mutableStateMapOf<Int, LazyResult<ImageBitmap>>()
    private val _entriesResponse = mutableStateOf<LazyResult<EntriesGetResponse>>(None())
    private val _enclosuresByEntryId = mutableStateMapOf<Int, LazyResult<Enclosure>>()

    val source = mutableStateOf<Source>(Source.Unread)
    val offset = mutableIntStateOf(0)
    val categories: State<LazyResult<List<Category>>> = _categories
    val feeds: State<LazyResult<List<Feed>>> = _feeds
    val feedCounters: State<LazyResult<FeedCountersResponse>> = _feedCounters
    val iconsById: Map<Int, LazyResult<ImageBitmap>> = _iconsById
    val entriesResponse: State<LazyResult<EntriesGetResponse>> = _entriesResponse
    val enclosuresByEntryId: Map<Int, LazyResult<Enclosure>> = _enclosuresByEntryId

    override suspend fun initialize(initData: Unit) {
        repository = repositoryFactory.create()
    }

    suspend fun reloadCategories() {
        lazyLoad(_categories) {
            repository.getAllCategories()
        }
    }

    suspend fun reloadFeeds() {
        lazyLoad(_feeds) {
            repository.getAllFeeds()
        }
    }

    suspend fun reloadIcon(id: Int) {
        lazyLoad(_iconsById, id) {
            val icon = repository.getIconById(
                id = id,
                origin = Origin.LocalThenRemote
            )
            val decodedBytes = Base64.decode(
                icon.data.substringAfter(","),
                Base64.DEFAULT
            )
            BitmapFactory.decodeByteArray(
                decodedBytes,
                0,
                decodedBytes.size
            ).asImageBitmap()
        }
    }

    suspend fun reloadFeedCounters() {
        lazyLoad(_feedCounters) {
            repository.getFeedCounters()
        }
    }

    suspend fun reloadEntriesAndEnclosures() {
        lazyLoad(_entriesResponse) {
            source.value.let {
                when (it) {
                    is Source.Unread ->
                        repository.getAllEntries(
                            read = false,
                            offset = offset.intValue,
                            globallyVisible = true,
                            limit = PAGE_ENTRY_COUNT
                        )
                    is Source.Read ->
                        repository.getAllEntries(
                            read = true,
                            offset = offset.intValue,
                            limit = PAGE_ENTRY_COUNT
                        )
                    is Source.Starred ->
                        repository.getAllEntries(
                            starred = true,
                            offset = offset.intValue,
                            limit = PAGE_ENTRY_COUNT
                        )
                    is Source.Category ->
                        repository.getEntriesFromCategory(
                            categoryId = it.id,
                            read = false,
                            offset = offset.intValue,
                            limit = PAGE_ENTRY_COUNT
                        )
                    is Source.Feed ->
                        repository.getEntriesFromFeed(
                            feedId = it.id,
                            read = false,
                            offset = offset.intValue,
                            limit = PAGE_ENTRY_COUNT
                        )
                    is Source.Search ->
                        repository.getAllEntries(
                            search = it.term,
                            offset = offset.intValue,
                            limit = PAGE_ENTRY_COUNT
                        )
                }
            }
        }

        entriesResponse.value.ifSuccess { entriesResponse ->
            entriesResponse.entries.forEach { entry ->
                lazyLoad(_enclosuresByEntryId, entry.id) {
                    repository.getEnclosuresByEntryId(entry.id).firstOrNull()
                }
            }
        }
    }

    suspend fun markAsRead(entryIds: List<Int>) {
        repository.updateEntriesRead(
            ids = entryIds,
            read = true
        )
    }
}
