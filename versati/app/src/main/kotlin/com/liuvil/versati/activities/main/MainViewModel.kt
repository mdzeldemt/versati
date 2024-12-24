package com.liuvil.versati.activities.main

import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.api.data.SortDirection
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

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

enum class Status {
    UNINITIALIZED,
    LOADING,
    IDLE
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseViewModel<Unit>() {

    private val _status = MutableStateFlow(Status.UNINITIALIZED)
    private val _entries = MutableStateFlow<List<Entry>>(emptyList())

    val status: StateFlow<Status> = _status
    val entries: StateFlow<List<Entry>> = _entries

    suspend fun loadEntries() {
        _status.value = Status.LOADING

        _entries.value = minifluxApi.getEntries(
            direction = SortDirection.DESCENDING,
            limit = 10
        ).entries.map { entry ->
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
        }

        _status.value = Status.IDLE
    }
}

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