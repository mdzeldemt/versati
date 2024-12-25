package com.liuvil.versati.activities.main

import com.liuvil.versati.activities.main.drawer.Category
import com.liuvil.versati.activities.main.drawer.Feed
import com.liuvil.versati.activities.main.drawer.SourceTree
import com.liuvil.versati.activities.main.entry_list.Entry
import com.liuvil.versati.activities.main.entry_list.buildFromAPIModel
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.EntriesUpdateRequest
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.api.data.SortDirection
import com.liuvil.versati.framework.viewmodel.BaseStatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface SourceSelection {
    data object AllEntries: SourceSelection
    data class Category(val id: Int): SourceSelection
    data class Feed(val id: Int): SourceSelection
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseStatefulViewModel<Unit>() {

    private val _sourceTree = MutableStateFlow(SourceTree(listOf()))
    private val _entries = MutableStateFlow<List<Entry>>(emptyList())

    private val _selectedSource = MutableStateFlow<SourceSelection>(SourceSelection.AllEntries)

    val sourceTree: StateFlow<SourceTree> = _sourceTree
    val entries: StateFlow<List<Entry>> = _entries

    val selectedSource: StateFlow<SourceSelection> = _selectedSource

    suspend fun loadAll() {
        loadSourceTree()
        loadEntries()
    }

    private suspend fun loadSourceTree() {
        val categories = minifluxApi.getCategories()
        val feedsByCategoryId = minifluxApi.getFeeds().groupBy { it.category.id }

        _sourceTree.value = SourceTree(
            categories.map { category ->
                Category(
                    id = category.id,
                    title = category.title,
                    feeds = feedsByCategoryId.getOrDefault(category.id, listOf())
                        .map { feed ->
                            Feed(
                                id = feed.id,
                                title = feed.title
                            )
                        }
                )
            }
        )
    }

    suspend fun loadEntries() {
        _selectedSource.value.let {
            when (it) {
                is SourceSelection.AllEntries -> loadAllEntries()
                is SourceSelection.Category -> loadEntriesFromCategory(it.id)
                is SourceSelection.Feed -> loadEntriesFromFeed(it.id)
            }
        }
    }

    suspend fun markPageAsRead() {
        minifluxApi.updateEntries(
            EntriesUpdateRequest(
                entryIds = _entries.value.map { it.id },
                status = EntryStatus.READ
            )
        )
    }

    fun selectSource(source: SourceSelection) {
        _selectedSource.value = source
    }

    private suspend fun loadAllEntries() {
        _entries.value = minifluxApi.getEntries(
            status = EntryStatus.UNREAD,
            direction = SortDirection.DESCENDING,
            limit = 10
        ).entries.map { entry ->
            buildFromAPIModel(entry)
        }
    }

    private suspend fun loadEntriesFromCategory(id: Int) {
        _entries.value = minifluxApi.getCategoryEntries(
            categoryId = id,
            direction = SortDirection.DESCENDING,
            limit = 10
        ).entries.map { entry ->
            buildFromAPIModel(entry)
        }
    }

    private suspend fun loadEntriesFromFeed(id: Int) {
        _entries.value = minifluxApi.getFeedEntries(
            feedId = id,
            direction = SortDirection.DESCENDING,
            limit = 10
        ).entries.map { entry ->
            buildFromAPIModel(entry)
        }
    }
}
