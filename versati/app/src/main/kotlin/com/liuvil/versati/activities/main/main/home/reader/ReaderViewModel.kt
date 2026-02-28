package com.liuvil.versati.activities.main.main.home.reader

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.framework.html.extractImageURLs
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.repository.Origin
import com.liuvil.versati.repository.Repository
import com.liuvil.versati.repository.data.Enclosure
import com.liuvil.versati.repository.data.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

data class InitData(
    val entryId: Int
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
    val imageURL: URL?
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repositoryFactory: RepositoryFactory
): BaseViewModel<InitData>() {

    private var entryId: Int = -1

    private lateinit var repository: Repository

    private val _entry = MutableStateFlow<Entry?>(null)
    private val _starred = MutableStateFlow(false)

    private val _getEntryStatus = MutableStateFlow<Status>(Status.Loading)
    private val _toggleStarredStatus = MutableStateFlow<Status>(Status.Success)

    val entry = _entry.asStateFlow()
    val starred = _starred.asStateFlow()

    val getEntryStatus = _getEntryStatus.asStateFlow()
    val toggleStarredStatus = _toggleStarredStatus.asStateFlow()

    override suspend fun initialize(initData: InitData) {
        entryId = initData.entryId
        repository = repositoryFactory.create()
    }

    fun onLoadEntry() {
        viewModelScope.launch {
            _getEntryStatus.value = Status.Loading

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
            } catch (reason: Throwable) {
                _getEntryStatus.value = Status.Failure(reason)
                return@launch
            }

            val document = Jsoup.parse(entry.content)
            val imageURL =
                if (extractImageURLs(document).isEmpty()) {
                    enclosures.firstOrNull()?.url
                } else {
                    null
                }

            _entry.value = Entry(
                title = entry.title,
                content = entry.content,
                url = entry.url,
                feedTitle = feed.title,
                author = entry.author.takeIf { it.isNotEmpty() },
                createdAt = entry.createdAt,
                publishedAt = entry.publishedAt,
                read = entry.read,
                imageURL = imageURL
            )
            _starred.value = entry.starred

            _getEntryStatus.value = Status.Success
        }
    }

    fun onToggleStarred() {
        viewModelScope.launch {
            _toggleStarredStatus.value = Status.Loading

            runCatching {
                repository.toggleEntryStarred(entryId)
            }.onSuccess {
                _starred.update { !it }

                _toggleStarredStatus.value = Status.Success
            }.onFailure { reason ->
                _toggleStarredStatus.value = Status.Failure(reason)
            }
        }
    }
}
