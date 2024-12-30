package com.liuvil.versati.activities.main.entry

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.api.MinifluxApi
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
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
    private val _entry = mutableStateOf<LazyResult<Entry>>(None())
    private val _enclosure = mutableStateOf<LazyResult<Enclosure>>(None())

    val entry: State<LazyResult<Entry>> = _entry
    val enclosure: State<LazyResult<Enclosure>> = _enclosure

    override suspend fun initialize(initData: Int) {
        _entryId = initData
    }

    suspend fun loadEntry() {
        _entryId?.let { entryId ->
            lazyLoad(_entry) {
                val entry = minifluxApi.getEntry(entryId)
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

    suspend fun loadEnclosure(id: Int) {
        lazyLoad(_enclosure) {
            Enclosure(url = minifluxApi.getEnclosure(id).url)
        }
    }

}
