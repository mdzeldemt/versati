package com.liuvil.versati.activities.main.entry

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.api.miniflux.MinifluxAPI
import com.liuvil.versati.api.miniflux.data.EntryStatus
import com.liuvil.versati.framework.lazy.Failure
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.Success
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
    val createdAt: OffsetDateTime,
    val publishedAt: OffsetDateTime,
    val read: Boolean,
    val enclosureId: Int?
)

data class Enclosure(
    val url: URL
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val minifluxApi: MinifluxAPI
): BaseViewModel<Int>() {

    private var _entryId: Int? = null
    private val _entry = mutableStateOf<LazyResult<Entry>>(None())
    private val _enclosure = mutableStateOf<LazyResult<Enclosure>>(None())
    private val _starred = mutableStateOf<LazyResult<Boolean>>(None())

    val entry: State<LazyResult<Entry>> = _entry
    val enclosure: State<LazyResult<Enclosure>> = _enclosure
    val starred: State<LazyResult<Boolean>> = _starred

    override suspend fun initialize(initData: Int) {
        _entryId = initData
    }

    suspend fun loadEntry() {
        _entryId?.let { entryId ->
            _entry.value = Loading()
            _starred.value = Loading()

            val entry = try {
                minifluxApi.getEntry(entryId)
            } catch (exception: Exception) {
                _entry.value = Failure(exception)
                _starred.value = Failure(exception)
                return
            }

            _entry.value = Success(
                Entry(
                    title = entry.title,
                    content = entry.content,
                    url = entry.url,
                    feedTitle = entry.feed.title,
                    author = entry.author.let {
                        it.ifEmpty { null }
                    },
                    createdAt = entry.createdAt,
                    publishedAt = entry.publishedAt,
                    read = entry.status == EntryStatus.READ,
                    enclosureId = entry.enclosures.firstOrNull()?.id
                )
            )
            _starred.value = Success(entry.starred)
        }
    }

    suspend fun loadEnclosure(id: Int) {
        lazyLoad(_enclosure) {
            Enclosure(url = minifluxApi.getEnclosure(id).url)
        }
    }

    suspend fun toggleStarred() {
        _entryId?.let { entryId ->
            _starred.value.ifSuccess { starred ->
                _starred.value = Loading()

                try {
                    minifluxApi.toggleEntryBookmark(entryId)
                } catch (exception: Exception) {
                    _starred.value = Failure(exception)
                    return
                }

                _starred.value = Success(!starred)
            }
        }
    }
}
