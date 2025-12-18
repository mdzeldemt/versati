package com.liuvil.versati.activities.main.entry

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.activities.main.home.RepositoryFactory
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

data class InitData(
    val serverID: Int,
    val entryID: Int
)

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
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private lateinit var repository: Repository
    private var entryID: Int = -1

    private val _entry = mutableStateOf<LazyResult<Entry>>(None())
    private val _starred = mutableStateOf<LazyResult<Boolean>>(None())

    val entry: State<LazyResult<Entry>> = _entry
    val starred: State<LazyResult<Boolean>> = _starred

    override suspend fun initialize(initData: InitData) {
        repository = repositoryFactory.create(initData.serverID)
        entryID = initData.entryID
    }

    suspend fun loadEntry() {
        _entry.value = Loading()
        _starred.value = Loading()

        val entry: com.liuvil.versati.repository.data.Entry
        val feed: Feed
        val enclosures: List<Enclosure>
        try {
            entry = repository.getEntryById(
                id = entryID,
                origin = Origin.LocalThenRemote
            )
            feed = repository.getFeedById(
                id = entry.feedId,
                origin = Origin.LocalThenRemote
            )
            enclosures = repository.getEnclosuresByEntryId(
                entryId = entryID,
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

    suspend fun toggleStarred() {
        _starred.value.ifSuccess { starred ->
            _starred.value = Loading()

            try {
                repository.toggleEntryStarred(entryID)
            } catch (exception: Exception) {
                _starred.value = Failure(exception)
                return
            }

            _starred.value = Success(!starred)
        }
    }
}
