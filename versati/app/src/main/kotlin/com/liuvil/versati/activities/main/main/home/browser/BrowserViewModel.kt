package com.liuvil.versati.activities.main.main.home.browser

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.liuvil.versati.activities.main.main.home.browser.use_case.AddCategoryUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.AddFeedUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.EditCategoryUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.EditFeedUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.GetAllCategoriesUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.GetAllFeedsUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.GetEntriesUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.GetFeedUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.GetIconUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.MarkEntriesAsReadUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.RefreshFeedUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.RemoveCategoryUseCase
import com.liuvil.versati.activities.main.main.home.browser.use_case.RemoveFeedUseCase
import com.liuvil.versati.framework.api.decodeBitmap
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.framework.viewmodel.status.fold
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.math.max

// TODO: Make configurable
const val PAGE_ENTRY_COUNT = 10

sealed interface Source {
    data object Unread: Source
    data object History: Source
    data object Starred: Source
    data class Category(val id: Int): Source
    data class Feed(val id: Int): Source
    data class Search(val term: String): Source
}

internal data class Entry(
    val id: Int,
    val title: String,
    val url: URL,
    val feedId: Int,
    val text: String,
    val imageUrl: URL?,
    val isRead: Boolean,
    val publishedAt: OffsetDateTime
)

internal sealed class Event {
    object AddCategory {
        data class Success(val categoryId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object EditCategory {
        data class Success(val categoryId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object RemoveCategory {
        data class Success(val categoryId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object RefreshFeed {
        data class Success(val feedId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object AddFeed {
        data class Success(val feedId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object EditFeed {
        data class Success(val feedId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object RemoveFeed {
        data class Success(val feedId: Int): Event()
        data class Failure(val reason: Throwable): Event()
    }

    object LoadEntries {
        data object Start: Event()
    }
}

@HiltViewModel
internal class BrowserViewModel @Inject constructor(
    private val getAllCategories: GetAllCategoriesUseCase,
    private val addCategory: AddCategoryUseCase,
    private val editCategory: EditCategoryUseCase,
    private val removeCategory: RemoveCategoryUseCase,
    private val getAllFeeds: GetAllFeedsUseCase,
    private val getFeed: GetFeedUseCase,
    private val refreshFeed: RefreshFeedUseCase,
    private val addFeed: AddFeedUseCase,
    private val editFeed: EditFeedUseCase,
    private val removeFeed: RemoveFeedUseCase,
    private val getIcon: GetIconUseCase,
    private val getEntries: GetEntriesUseCase,
    private val markEntriesAsRead: MarkEntriesAsReadUseCase
): BaseViewModel<Unit>() {
    private val _source = MutableStateFlow<Source>(Source.Unread)
    private val _offset = MutableStateFlow(0)

    private val _categoriesById = MutableStateFlow(emptyMap<Int, Category>())
    private val _feedsById = MutableStateFlow(emptyMap<Int, Feed>())
    private val _iconsById = MutableStateFlow(emptyMap<Int, ImageBitmap>())
    private val _entriesById = MutableStateFlow(emptyMap<Int, Entry>())
    private val _totalEntries = MutableStateFlow(0)

    private val _getCategoriesStatus = MutableStateFlow<Status>(Status.Success)
    private val _addCategoryStatus = MutableStateFlow<Status>(Status.Success)
    private val _editCategoryStatus = MutableStateFlow<Status>(Status.Success)
    private val _removeCategoryStatus = MutableStateFlow<Status>(Status.Success)
    private val _getFeedsStatus = MutableStateFlow<Status>(Status.Success)
    private val _addFeedStatus = MutableStateFlow<Status>(Status.Success)
    private val _editFeedStatus = MutableStateFlow<Status>(Status.Success)
    private val _removeFeedStatus = MutableStateFlow<Status>(Status.Success)
    private val _getEntriesStatus = MutableStateFlow<Status>(Status.Success)
    private val _markEntriesAsReadStatus = MutableStateFlow<Status>(Status.Success)

    private val _events = MutableSharedFlow<Event>()

    val source = _source.asStateFlow()
    val offset = _offset.asStateFlow()

    val categoriesById = _categoriesById.asStateFlow()
    val feedsById = _feedsById.asStateFlow()
    val iconsById = _iconsById.asStateFlow()
    val entriesById = _entriesById.asStateFlow()
    val totalEntries = _totalEntries.asStateFlow()

    val categoriesStatus = combine(
        _getCategoriesStatus,
        _addCategoryStatus,
        _editCategoryStatus,
        _removeCategoryStatus
    ) { statuses ->
        fold(*statuses)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Status.Loading
    )

    val feedsStatus = combine(
        _getFeedsStatus,
        _addFeedStatus,
        _editFeedStatus,
        _removeFeedStatus
    ) { statuses ->
        fold(*statuses)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Status.Loading
    )

    val entriesStatus = _getEntriesStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Status.Loading
        )

    val events = _events.asSharedFlow()

    fun onReloadAllCategories() {
        viewModelScope.launch {
            _getCategoriesStatus.value = Status.Loading

            getAllCategories()
                .onSuccess { categories ->
                    _categoriesById.value = categories.associateBy { it.id }
                    _getCategoriesStatus.value = Status.Success
                }
                .onFailure { reason ->
                    _getCategoriesStatus.value = Status.Failure(reason)
                }
        }
    }

    fun onReloadAllFeedsAndIcons() {
        viewModelScope.launch {
            _getFeedsStatus.value = Status.Loading

            getAllFeeds()
                .onSuccess { feeds ->
                    _feedsById.value = feeds.associateBy { it.id }
                    _getFeedsStatus.value = Status.Success

                    feeds
                        .map { feed ->
                            async {
                                getIcon(feed.iconId)
                            }
                        }
                        .awaitAll()
                        .mapNotNull { it.getOrNull() }
                        .forEach { icon ->
                            _iconsById.update {
                                it + (icon.id to decodeBitmap(icon.data).asImageBitmap())
                            }
                        }
                }
                .onFailure { reason ->
                    _getFeedsStatus.value = Status.Failure(reason)
                }
        }
    }

    fun onReloadAllEntries() {
        viewModelScope.launch {
            _getEntriesStatus.value = Status.Loading

            _events.emit(Event.LoadEntries.Start)

            getEntries(_source.value, offset.value, PAGE_ENTRY_COUNT)
                .onSuccess { (entries, total) ->
                    _entriesById.value = entries.associateBy { it.id }
                    _totalEntries.value = total
                    _getEntriesStatus.value = Status.Success
                }
                .onFailure {
                    _getEntriesStatus.value = Status.Failure(it)
                }
        }
    }

    fun onSelectSource(
        source: Source
    ) {
        viewModelScope.launch {
            _source.value = source
            _offset.value = 0

            _getEntriesStatus.value = Status.Loading

            _events.emit(Event.LoadEntries.Start)

            getEntries(_source.value, offset.value, PAGE_ENTRY_COUNT)
                .onSuccess { (entries, total) ->
                    _entriesById.value = entries.associateBy { it.id }
                    _totalEntries.value = total
                    _getEntriesStatus.value = Status.Success
                }
                .onFailure {
                    _getEntriesStatus.value = Status.Failure(it)
                }
        }
    }

    fun onGoToPage(
        page: Int
    ) {
        viewModelScope.launch {
            _offset.value = (page - 1) * PAGE_ENTRY_COUNT

            _getEntriesStatus.value = Status.Loading

            _events.emit(Event.LoadEntries.Start)

            getEntries(_source.value, offset.value, PAGE_ENTRY_COUNT)
                .onSuccess { (entries, total) ->
                    _entriesById.value = entries.associateBy { it.id }
                    _totalEntries.value = total
                    _getEntriesStatus.value = Status.Success
                }
                .onFailure {
                    _getEntriesStatus.value = Status.Failure(it)
                }
        }
    }

    fun onGoToPreviousPage() {
        viewModelScope.launch {
            _offset.update { max(it - PAGE_ENTRY_COUNT, 0) }

            _getEntriesStatus.value = Status.Loading

            _events.emit(Event.LoadEntries.Start)

            getEntries(_source.value, offset.value, PAGE_ENTRY_COUNT)
                .onSuccess { (entries, total) ->
                    _entriesById.value = entries.associateBy { it.id }
                    _totalEntries.value = total
                    _getEntriesStatus.value = Status.Success
                }
                .onFailure {
                    _getEntriesStatus.value = Status.Failure(it)
                }
        }
    }

    fun onGoToNextPage() {
        viewModelScope.launch {
            _offset.update { it + PAGE_ENTRY_COUNT }

            _getEntriesStatus.value = Status.Loading

            _events.emit(Event.LoadEntries.Start)

            getEntries(_source.value, offset.value, PAGE_ENTRY_COUNT)
                .onSuccess { (entries, total) ->
                    _entriesById.value = entries.associateBy { it.id }
                    _totalEntries.value = total
                    _getEntriesStatus.value = Status.Success
                }
                .onFailure {
                    _getEntriesStatus.value = Status.Failure(it)
                }
        }
    }

    fun onMarkAllEntriesAsRead() {
        viewModelScope.launch {
            _markEntriesAsReadStatus.value = Status.Loading

            val entryIds = entriesById.value.keys.toList()
            markEntriesAsRead(entryIds)
                .onSuccess {
                    _markEntriesAsReadStatus.value = Status.Success
                }.onFailure {
                    _markEntriesAsReadStatus.value = Status.Failure(it)
                }
        }
    }

    fun onAddCategory(
        title: String
    ) {
        viewModelScope.launch {
            _addCategoryStatus.value = Status.Loading

            addCategory(title)
                .onSuccess { category ->
                    _categoriesById.update { it + (category.id to category) }
                    _addCategoryStatus.value = Status.Success

                    _events.emit(Event.AddCategory.Success(category.id))
                }
                .onFailure { reason ->
                    _addCategoryStatus.value = Status.Failure(reason)

                    _events.emit(Event.AddCategory.Failure(reason))
                }
        }
    }

    fun onEditCategory(
        id: Int,
        title: String
    ) {
        viewModelScope.launch {
            _editCategoryStatus.value = Status.Loading

            editCategory(id, title)
                .onSuccess { category ->
                    _categoriesById.update { it + (id to category) }
                    _editCategoryStatus.value = Status.Success

                    _events.emit(Event.EditCategory.Success(category.id))
                }
                .onFailure { reason ->
                    _editCategoryStatus.value = Status.Failure(reason)

                    _events.emit(Event.EditCategory.Failure(reason))
                }
        }
    }

    fun onRemoveCategory(
        id: Int
    ) {
        viewModelScope.launch {
            _removeCategoryStatus.value = Status.Loading

            removeCategory(id)
                .onSuccess {
                    _categoriesById.update { it - id }
                    _removeCategoryStatus.value = Status.Success

                    _events.emit(Event.RemoveCategory.Success(id))
                }
                .onFailure { reason ->
                    _removeCategoryStatus.value = Status.Failure(reason)

                    _events.emit(Event.RemoveCategory.Failure(reason))
                }
        }
    }

    fun onRefreshFeed(
        id: Int
    ) {
        viewModelScope.launch {
            refreshFeed(id)
                .onSuccess {
                    _events.emit(Event.RefreshFeed.Success(id))
                }
                .onFailure { reason ->
                    _events.emit(Event.RefreshFeed.Failure(reason))
                }

            getFeed(id)
                .onSuccess { feed ->
                    _feedsById.update { it + (id to feed) }
                }
        }
    }

    fun onAddFeed(
        feedUrl: URL,
        categoryId: Int,
    ) {
        viewModelScope.launch {
            _addFeedStatus.value = Status.Loading

            addFeed(feedUrl, categoryId)
                .onSuccess { feed ->
                    _feedsById.update { it + (feed.id to feed) }
                    _addFeedStatus.value = Status.Success

                    _events.emit(Event.AddFeed.Success(feed.id))

                    getIcon(feed.iconId)
                        .onSuccess { icon ->
                            _iconsById.update {
                                it + (icon.id to decodeBitmap(icon.data).asImageBitmap())
                            }
                        }
                }
                .onFailure { reason ->
                    _addFeedStatus.value = Status.Failure(reason)

                    _events.emit(Event.AddFeed.Failure(reason))
                }
        }
    }

    fun onEditFeed(
        id: Int,
        title: String,
        feedUrl: URL,
        categoryId: Int
    ) {
        viewModelScope.launch {
            _editFeedStatus.value = Status.Loading

            editFeed(id, title, feedUrl, categoryId)
                .onSuccess { feed ->
                    _feedsById.update { it + (feed.id to feed) }
                    _editFeedStatus.value = Status.Success

                    _events.emit(Event.EditFeed.Success(feed.id))
                }
                .onFailure { reason ->
                    _editFeedStatus.value = Status.Failure(reason)

                    _events.emit(Event.AddFeed.Failure(reason))
                }
        }
    }

    fun onRemoveFeed(
        id: Int
    ) {
        viewModelScope.launch {
            _removeFeedStatus.value = Status.Loading

            removeFeed(id)
                .onSuccess {
                    _feedsById.update { it - id }
                    _removeFeedStatus.value = Status.Success

                    _events.emit(Event.RemoveFeed.Success(id))
                }
                .onFailure { reason ->
                    _removeFeedStatus.value = Status.Failure(reason)

                    _events.emit(Event.AddFeed.Failure(reason))
                }
        }
    }
}
