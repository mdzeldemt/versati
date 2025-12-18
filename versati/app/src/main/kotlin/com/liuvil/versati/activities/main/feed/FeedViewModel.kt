package com.liuvil.versati.activities.main.feed

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.liuvil.versati.activities.main.home.RepositoryFactory
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.PreferenceStore
import com.liuvil.versati.preferences.ServerRepository
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.api.data.EntriesGetResponse
import com.liuvil.versati.repository.api.data.FeedCountersResponse
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Enclosure
import com.liuvil.versati.repository.data.Feed
import com.liuvil.versati.repository.data.Icon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class InitData(
    val serverID: Int
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private lateinit var repository: Repository

    private val _categories = mutableStateOf<LazyResult<List<Category>>>(None())
    private val _feeds = mutableStateOf<LazyResult<List<Feed>>>(None())
    private val _feedCounters = mutableStateOf<LazyResult<FeedCountersResponse>>(None())
    private val _iconsById = mutableStateMapOf<Int, LazyResult<Icon>>()
    private val _entriesResponse = mutableStateOf<LazyResult<EntriesGetResponse>>(None())
    private val _enclosuresByEntryId = mutableStateMapOf<Int, LazyResult<Enclosure>>()

    val source = mutableStateOf<Source>(Source.Unread)
    val offset = mutableIntStateOf(0)
    val categories: State<LazyResult<List<Category>>> = _categories
    val feeds: State<LazyResult<List<Feed>>> = _feeds
    val feedCounters: State<LazyResult<FeedCountersResponse>> = _feedCounters
    val iconsById: Map<Int, LazyResult<Icon>> = _iconsById
    val entriesResponse: State<LazyResult<EntriesGetResponse>> = _entriesResponse
    val enclosuresByEntryId: Map<Int, LazyResult<Enclosure>> = _enclosuresByEntryId

    override suspend fun initialize(initData: InitData) {
        repository = repositoryFactory.create(initData.serverID)
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
            repository.getIconById(
                id = id,
                origin = Origin.LocalThenRemote
            )
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
