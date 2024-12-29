package com.liuvil.versati.activities.main.entry

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

data class Entry(
    val title: String,
    val content: String,
    val url: URL,
    val feedTitle: String,
    val author: String?,
    val publishedAt: OffsetDateTime,
    val enclosureId: Int?
)

data class Enclosure(
    val url: URL
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val minifluxApi: MinifluxApi
): BaseViewModel<Int>() {

    private var _entryId: Int? = null
    private val _entry = mutableStateOf<Entry?>(null)
    private val _enclosure = mutableStateOf<Enclosure?>(null)

    val entry: State<Entry?> = _entry
    val enclosure: State<Enclosure?> = _enclosure

    override suspend fun initialize(initData: Int) {
        _entryId = initData
    }

    suspend fun loadAll() {
        loadEntry()

        _entry.value?.enclosureId?.let {
            loadEnclosure(id = it)
        }
    }

    private suspend fun loadEntry() {
        _entryId?.let { entryId ->
            _entry.value = minifluxApi.getEntry(entryId).let { entry ->
                Entry(
                    title = entry.title,
                    content = entry.content,
                    url = entry.url,
                    feedTitle = entry.feed.title,
                    author = entry.author.let {
                        it.ifEmpty { null }
                    },
                    publishedAt = entry.publishedAt,
                    enclosureId = entry.enclosures.firstOrNull()?.id
                )
            }
        }
    }

    private suspend fun loadEnclosure(id: Int) {
        _enclosure.value = Enclosure(url = minifluxApi.getEnclosure(id).url)
    }

}
