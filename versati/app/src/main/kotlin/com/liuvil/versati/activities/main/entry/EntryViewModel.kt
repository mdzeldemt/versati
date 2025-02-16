package com.liuvil.versati.activities.main.entry

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.framework.lazy.Failure
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.Success
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.repository.data.Enclosure
import com.liuvil.versati.repository.data.Feed
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
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
    val enclosureUrl: URL?
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val repository: Repository
): BaseViewModel<Int>() {

    private var _entryId: Int? = null
    private val _entry = mutableStateOf<LazyResult<Entry>>(None())
    private val _starred = mutableStateOf<LazyResult<Boolean>>(None())

    val entry: State<LazyResult<Entry>> = _entry
    val starred: State<LazyResult<Boolean>> = _starred

    override suspend fun initialize(initData: Int) {
        _entryId = initData
    }

    suspend fun loadEntry() {
        _entryId?.let { entryId ->
            _entry.value = Loading()
            _starred.value = Loading()

            val entry: com.liuvil.versati.repository.data.Entry
            val feed: Feed
            val enclosures: List<Enclosure>
            try {
                entry = repository.getEntryById(
                    id = entryId,
                    origin = Origin.LocalThenRemote
                )
                feed = repository.getFeedById(
                    id = entry.feedId,
                    origin = Origin.LocalThenRemote
                )
                enclosures = repository.getEnclosuresByEntryId(
                    entryId = entryId,
                    origin = Origin.LocalThenRemote
                )
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
                    feedTitle = feed.title,
                    author = entry.author.let {
                        it.ifEmpty { null }
                    },
                    createdAt = entry.createdAt,
                    publishedAt = entry.publishedAt,
                    read = entry.read,
                    enclosureUrl = enclosures.firstOrNull()?.url
                )
            )
            _starred.value = Success(entry.starred)
        }
    }

    suspend fun toggleStarred() {
        _entryId?.let { entryId ->
            _starred.value.ifSuccess { starred ->
                _starred.value = Loading()

                try {
                    repository.toggleEntryStarred(entryId)
                } catch (exception: Exception) {
                    _starred.value = Failure(exception)
                    return
                }

                _starred.value = Success(!starred)
            }
        }
    }
}
