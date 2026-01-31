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
import com.liuvil.versati.framework.lazy.Failure
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.Success
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.api.data.EntriesGetResponse
import com.liuvil.versati.repository.api.data.EntryStatus
import com.liuvil.versati.repository.api.data.FeedCountersResponse
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

data class Entry(
    val id: Int,
    val title: String,
    val url: URL,
    val feedID: Int,
    val content: String,
    val imageURL: URL?,
    val isRead: Boolean,
    val publishedAt: OffsetDateTime
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<Unit>() {

    private lateinit var repository: Repository

    private val _categories = mutableStateOf<LazyResult<List<Category>>>(None())
    private val _feeds = mutableStateOf<LazyResult<List<Feed>>>(None())
    private val _feedCounters = mutableStateOf<LazyResult<FeedCountersResponse>>(None())
    private val _iconsById = mutableStateMapOf<Int, LazyResult<ImageBitmap>>()
    private val _entries = mutableStateOf<LazyResult<List<Entry>>>(None())
    private val _totalEntries = mutableStateOf<LazyResult<Int>>(None())

    val source = mutableStateOf<Source>(Source.Unread)
    val offset = mutableIntStateOf(0)
    val categories: State<LazyResult<List<Category>>> = _categories
    val feeds: State<LazyResult<List<Feed>>> = _feeds
    val feedCounters: State<LazyResult<FeedCountersResponse>> = _feedCounters
    val iconsById: Map<Int, LazyResult<ImageBitmap>> = _iconsById
    val entries: State<LazyResult<List<Entry>>> = _entries
    val totalEntries: State<LazyResult<Int>> = _totalEntries

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

    suspend fun reloadEntries() {
        _entries.value = Loading()
        _totalEntries.value = Loading()

        val entriesResponse: EntriesGetResponse

        try {
            entriesResponse = source.value.let {
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
        } catch (exception: Exception) {
            _entries.value = Failure(exception)
            _feeds.value = Failure(exception)
            return
        }

        _entries.value = Success(
            entriesResponse.entries
                .map {
                    Entry(
                        id = it.id,
                        title = it.title,
                        url = it.url,
                        feedID = it.feedId,
                        isRead = it.status == EntryStatus.READ,
                        content = it.content,
                        imageURL = it.enclosures.firstOrNull()?.url,
                        publishedAt = it.publishedAt
                    )
                }
        )
        _totalEntries.value = Success(entriesResponse.total)
    }

    suspend fun markAsRead(entryIds: List<Int>) {
        repository.updateEntriesRead(
            ids = entryIds,
            read = true
        )
    }
}
