package com.liuvil.versati.activities.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.EntriesUpdateRequest
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.api.data.SortDirection
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseViewModel<Unit>() {

    private val _categories = mutableStateOf<LazyResult<List<com.liuvil.versati.api.data.Category>>>(None())
    private val _feeds = mutableStateOf<LazyResult<List<com.liuvil.versati.api.data.Feed>>>(None())
    private val _feedIconsById = mutableStateMapOf<Int, LazyResult<com.liuvil.versati.api.data.Icon>>()
    private val _entries = mutableStateOf<LazyResult<List<com.liuvil.versati.api.data.Entry>>>(None())

    val selectedSource: MutableState<SourceSelection> = mutableStateOf(SourceSelection.Unread)
    val categories: State<LazyResult<List<com.liuvil.versati.api.data.Category>>> = _categories
    val feeds: State<LazyResult<List<com.liuvil.versati.api.data.Feed>>> = _feeds
    val feedIconsById: Map<Int, LazyResult<com.liuvil.versati.api.data.Icon>> = _feedIconsById
    val entries: State<LazyResult<List<com.liuvil.versati.api.data.Entry>>> = _entries

    suspend fun reloadCategories() {
        lazyLoad(_categories) {
            minifluxApi.getCategories()
        }
    }

    suspend fun reloadFeeds() {
        lazyLoad(_feeds) {
            minifluxApi.getFeeds()
        }
    }

    suspend fun reloadFeedIcon(id: Int) {
        lazyLoad(_feedIconsById, id) {
            minifluxApi.getFeedIcon(id)
        }
    }

    suspend fun reloadEntries() {
        lazyLoad(_entries) {
            selectedSource.value.let {
                when (it) {
                    is SourceSelection.Unread ->
                        minifluxApi.getEntries(
                            status = EntryStatus.UNREAD,
                            direction = SortDirection.DESCENDING,
                            limit = 10
                        ).entries
                    is SourceSelection.Category ->
                        minifluxApi.getCategoryEntries(
                            categoryId = it.id,
                            status = EntryStatus.UNREAD,
                            direction = SortDirection.DESCENDING,
                            limit = 10
                        ).entries
                    is SourceSelection.Feed ->
                        minifluxApi.getFeedEntries(
                            feedId = it.id,
                            status = EntryStatus.UNREAD,
                            direction = SortDirection.DESCENDING,
                            limit = 10
                        ).entries
                    is SourceSelection.Read ->
                        minifluxApi.getEntries(
                            status = EntryStatus.READ,
                            direction = SortDirection.DESCENDING,
                            limit = 10
                        ).entries
                }
            }
        }
    }

    suspend fun markAsRead(entryIds: List<Int>) {
        minifluxApi.updateEntries(
            EntriesUpdateRequest(
                entryIds = entryIds,
                status = EntryStatus.READ
            )
        )
    }

}
