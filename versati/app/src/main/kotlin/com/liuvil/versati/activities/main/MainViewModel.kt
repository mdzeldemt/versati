package com.liuvil.versati.activities.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseStatefulViewModel<Unit>() {

    private val _categories = mutableStateOf(emptyList<com.liuvil.versati.api.data.Category>())
    private val _feeds = mutableStateOf(emptyList<com.liuvil.versati.api.data.Feed>())
    private val _entries = mutableStateOf(emptyList<com.liuvil.versati.api.data.Entry>())

    val selectedSource: MutableState<SourceSelection> = mutableStateOf(SourceSelection.AllEntries)

    val sourceTree: State<SourceTree> = derivedStateOf {
        val feedsByCategoryId = _feeds.value.groupBy { it.category.id }
        SourceTree(
            _categories.value.map { category ->
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

    val entries: State<List<Entry>> = derivedStateOf {
        _entries.value.map { entry ->
            buildFromAPIModel(entry)
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

    suspend fun reloadCategories() {
        _categories.value = minifluxApi.getCategories()
    }

    suspend fun reloadFeeds() {
        _feeds.value = minifluxApi.getFeeds()
    }

    suspend fun reloadEntries() {
        _entries.value = selectedSource.value.let {
            when (it) {
                is SourceSelection.AllEntries ->
                    minifluxApi.getEntries(
                        status = EntryStatus.UNREAD,
                        direction = SortDirection.DESCENDING,
                        limit = 10
                    ).entries
                is SourceSelection.Category ->
                    minifluxApi.getCategoryEntries(
                        categoryId = it.id,
                        direction = SortDirection.DESCENDING,
                        limit = 10
                    ).entries
                is SourceSelection.Feed ->
                    minifluxApi.getFeedEntries(
                        feedId = it.id,
                        direction = SortDirection.DESCENDING,
                        limit = 10
                    ).entries
            }
        }
    }
}
