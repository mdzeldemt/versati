package com.liuvil.versati.activities.main.main.home.entry.browse

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.api.decodeBitmap
import com.liuvil.versati.framework.html.extractImageURLs
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
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jsoup.Jsoup
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

data class Entry(
    val id: Int,
    val title: String,
    val url: URL,
    val feedID: Int,
    val text: String,
    val imageURL: URL?,
    val isRead: Boolean,
    val publishedAt: OffsetDateTime
)

@HiltViewModel
class BrowseEntriesViewModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<Unit>() {

    private lateinit var repository: Repository

    private val _categories = mutableStateOf<LazyResult<List<Category>>>(None())
    private val _feeds = mutableStateOf<LazyResult<List<Feed>>>(None())
    private val _iconsById = mutableStateMapOf<Int, LazyResult<ImageBitmap>>()
    private val _entries = mutableStateOf<LazyResult<List<Entry>>>(None())
    private val _totalEntries = mutableStateOf<LazyResult<Int>>(None())

    val source = mutableStateOf<Source>(Source.Unread)
    val offset = mutableIntStateOf(0)

    val categories: State<LazyResult<List<Category>>> = _categories
    val feeds: State<LazyResult<List<Feed>>> = _feeds
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
            decodeBitmap(icon.data).asImageBitmap()
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
                    is Source.History ->
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
            _totalEntries.value = Failure(exception)
            return
        }

        _entries.value = Success(
            entriesResponse.entries
                .map {
                    val document = Jsoup.parse(it.content)
                    val text = document.text()
                    val imageURL =
                        extractImageURLs(document).firstOrNull()
                            ?: it.enclosures.firstOrNull()?.url

                    Entry(
                        id = it.id,
                        title = it.title,
                        url = it.url,
                        feedID = it.feedId,
                        isRead = it.status == EntryStatus.READ,
                        text = text,
                        imageURL = imageURL,
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

    suspend fun createCategory(
        title: String
    ): Int =
        repository.createCategory(
            title
        ).id

    suspend fun updateCategory(
        id: Int,
        title: String
    ) {
        repository.updateCategory(
            id,
            title
        )
    }

    suspend fun deleteCategory(
        id: Int
    ) {
        repository.deleteCategory(
            id
        )
    }

    suspend fun createFeed(
        feedUrl: URL,
        categoryId: Int,
    ): Int =
        repository.createFeed(
            feedUrl,
            categoryId
        )

    suspend fun updateFeed(
        id: Int,
        title: String,
        feedUrl: URL,
        categoryId: Int
    ) {
        repository.updateFeed(
            id,
            title,
            feedUrl,
            categoryId
        )
    }

    suspend fun deleteFeed(
        feedId: Int
    ) {
        repository.deleteFeed(
            id = feedId
        )
    }
}
