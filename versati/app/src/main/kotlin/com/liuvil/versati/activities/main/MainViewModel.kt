package com.liuvil.versati.activities.main

import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.EntriesUpdateRequest
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.api.data.SortDirection
import com.liuvil.versati.framework.viewmodel.BaseStatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

data class FeedTree(
    val categoryNodes: List<CategoryNode>
)

data class CategoryNode(
    val id: Int,
    val title: String,
    val feedNodes: List<FeedNode>
)

data class FeedNode(
    val id: Int,
    val title: String
)

data class Entry(
    val id: Int,
    val title: String,
    val feedTitle: String,
    val publishedAt: OffsetDateTime,
    val content: EntryContent,
    val enclosures: List<Enclosure>
)

data class EntryContent(
    val text: String,
    val imageURLs: List<URL>
)

data class Enclosure(
    val url: URL
)

sealed class Selection {
    data class Category(val id: Int): Selection()
    data class Feed(val id: Int): Selection()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseStatefulViewModel<Unit>() {

    private val _feedTree = MutableStateFlow(FeedTree(listOf()))
    private val _entries = MutableStateFlow<List<Entry>>(emptyList())

    private val _selection = MutableStateFlow<Selection?>(null)

    val feedTree: StateFlow<FeedTree> = _feedTree
    val entries: StateFlow<List<Entry>> = _entries

    val selection: StateFlow<Selection?> = _selection

    suspend fun loadAll() {
        loadFeedTree()
        loadEntries()
    }

    private suspend fun loadFeedTree() {
        val categories = minifluxApi.getCategories()
        val feedsByCategoryId = minifluxApi.getFeeds().groupBy { it.category.id }

        _feedTree.value = FeedTree(
            categories.map { category ->
                CategoryNode(
                    id = category.id,
                    title = category.title,
                    feedNodes = feedsByCategoryId.getOrDefault(category.id, listOf())
                        .map { feed ->
                            FeedNode(
                                id = feed.id,
                                title = feed.title
                            )
                        }
                )
            }
        )
    }

    suspend fun loadEntries() {
        _selection.value?.let {
            when (it) {
                is Selection.Category -> loadEntriesFromCategory(it.id)
                is Selection.Feed -> loadEntriesFromFeed(it.id)
            }
        } ?: loadMainEntries()
    }

    suspend fun markPageAsRead() {
        minifluxApi.updateEntries(
            EntriesUpdateRequest(
                entryIds = _entries.value.map { it.id },
                status = EntryStatus.READ
            )
        )
    }

    fun select(selection: Selection) {
        _selection.value = selection
    }

    private suspend fun loadMainEntries() {
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

private fun buildFromAPIModel(entry: com.liuvil.versati.api.data.Entry): Entry =
    Entry(
        id = entry.id,
        title = entry.title,
        feedTitle = entry.feed.title,
        publishedAt = entry.publishedAt,
        content = parseEntryContent(entry.content),
        enclosures = entry.enclosures.map { enclosure ->
            Enclosure(enclosure.url)
        }
    )

// TODO: Move to separate package
private fun parseEntryContent(entryContent: String): EntryContent {
    val document = Jsoup.parse(entryContent)
    return EntryContent(
        text = document.text(),
        imageURLs = document.getElementsByTag("img")
            .mapNotNull { it.attribute("src") }
            .map { URL(it.value) }
    )
}