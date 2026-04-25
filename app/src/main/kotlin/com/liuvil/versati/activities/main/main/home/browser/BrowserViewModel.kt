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
import com.liuvil.versati.preferences.PreferenceStore
import com.liuvil.versati.repository.data.Category
import com.liuvil.versati.repository.data.Feed
import com.liuvil.versati.repository.data.Icon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.math.max

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
    object LoadCategories {
        data class Failure(val reason: Throwable): Event()
    }

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

    object LoadFeeds {
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
        data object Success: Event()
        data class Failure(val reason: Throwable): Event()
    }

    object MarkAllEntriesAsRead {
        data object Success: Event()
        data class Failure(val reason: Throwable): Event()
    }
}

@HiltViewModel
internal class BrowserViewModel @Inject constructor(
    private val preferenceStore: PreferenceStore,
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

    private val _entriesStatus = MutableStateFlow<Status>(Status.Success)

    private val _events = MutableSharedFlow<Event>()

    val source = _source.asStateFlow()
    val offset = _offset.asStateFlow()

    val categoriesById = _categoriesById.asStateFlow()
    val feedsById = _feedsById.asStateFlow()
    val iconsById = _iconsById.asStateFlow()
    val entriesById = _entriesById.asStateFlow()
    val totalEntries = _totalEntries.asStateFlow()

    val entriesStatus = _entriesStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Status.Loading
        )

    val events = _events.asSharedFlow()

    fun onReloadAllCategories() {
        viewModelScope.launch {
            reloadAllCategories()
        }
    }

    fun onAddCategory(
        title: String
    ) {
        viewModelScope.launch {
            addCategory(title)
                .onSuccess {
                    reloadAllCategories()
                }
        }
    }

    fun onEditCategory(
        id: Int,
        title: String
    ) {
        viewModelScope.launch {
            editCategory(id, title)
        }
    }

    fun onRemoveCategory(
        id: Int
    ) {
        viewModelScope.launch {
            removeCategory(id)
        }
    }

    fun onReloadAllFeedsAndIcons() {
        viewModelScope.launch {
            reloadAllFeeds()
                .onSuccess { feeds ->
                    feeds.map { feed ->
                        launch {
                            reloadIcon(feed.iconId)
                        }
                    }
                }
        }
    }

    fun onRefreshFeed(
        id: Int
    ) {
        viewModelScope.launch {
            refreshFeed(id)
                .onSuccess {
                    reloadFeed(id)
                }
        }
    }

    fun onAddFeed(
        feedUrl: URL,
        categoryId: Int,
    ) {
        viewModelScope.launch {
            addFeed(feedUrl, categoryId)
                .onSuccess { feedId ->
                    reloadFeed(feedId)
                        .onSuccess { feed ->
                            reloadIcon(feed.iconId)
                        }
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
            editFeed(id, title, feedUrl, categoryId)
        }
    }

    fun onRemoveFeed(
        id: Int
    ) {
        viewModelScope.launch {
            removeFeed(id)
        }
    }

    fun onReloadAllEntries() {
        viewModelScope.launch {
            reloadAllEntries()
        }
    }

    fun onSelectSource(
        source: Source
    ) {
        viewModelScope.launch {
            _source.value = source
            _offset.value = 0

            reloadAllEntries()
        }
    }

    fun onGoToPreviousPage() {
        viewModelScope.launch {
            val entriesPerPage = preferenceStore.entriesPerPage.first()

            _offset.update { max(it - entriesPerPage, 0) }

            reloadAllEntries()
        }
    }

    fun onGoToNextPage() {
        viewModelScope.launch {
            val entriesPerPage = preferenceStore.entriesPerPage.first()

            _offset.update { it + entriesPerPage }

            reloadAllEntries()
        }
    }

    fun onMarkAllEntriesAsRead() {
        viewModelScope.launch {
            markAllEntriesAsRead()
                .onSuccess {
                    reloadAllEntries()
                }
        }
    }

    private suspend fun reloadAllCategories(): Result<List<Category>> {
        return getAllCategories.perform()
            .onSuccess { categories ->
                _categoriesById.value = categories.associateBy { it.id }
            }
            .onFailure { reason ->
                _events.emit(Event.LoadCategories.Failure(reason))
            }
    }

    private suspend fun addCategory(
        title: String
    ): Result<Int> {
        return addCategory.perform(title)
            .onSuccess { categoryId ->
                _events.emit(Event.AddCategory.Success(categoryId))
            }
            .onFailure { reason ->
                _events.emit(Event.AddCategory.Failure(reason))
            }
    }

    private suspend fun editCategory(
        id: Int,
        title: String
    ): Result<Category> {
        return editCategory.perform(id, title)
            .onSuccess { category ->
                _categoriesById.update { it + (id to category) }
                _events.emit(Event.EditCategory.Success(category.id))
            }
            .onFailure { reason ->
                _events.emit(Event.EditCategory.Failure(reason))
            }
    }

    private suspend fun removeCategory(
        id: Int
    ): Result<Unit> {
        return removeCategory.perform(id)
            .onSuccess {
                _categoriesById.update { it - id }
                _events.emit(Event.RemoveCategory.Success(id))
            }
            .onFailure { reason ->
                _events.emit(Event.RemoveCategory.Failure(reason))
            }
    }

    private suspend fun reloadAllFeeds(): Result<List<Feed>> {
        return getAllFeeds.perform()
            .onSuccess { feeds ->
                _feedsById.value = feeds.associateBy { it.id }
            }
            .onFailure { reason ->
                _events.emit(Event.LoadFeeds.Failure(reason))
            }
    }

    private suspend fun reloadFeed(
        id: Int
    ): Result<Feed> {
        return getFeed.perform(id)
            .onSuccess { feed ->
                _feedsById.update { it + (id to feed) }
            }
    }

    private suspend fun reloadIcon(
        id: Int
    ): Result<Icon> {
        return getIcon.perform(id)
            .onSuccess { icon ->
                _iconsById.update {
                    it + (icon.id to decodeBitmap(icon.data).asImageBitmap())
                }
            }
    }

    private suspend fun refreshFeed(
        id: Int
    ): Result<Unit> {
        return refreshFeed.perform(id)
            .onSuccess {
                _events.emit(Event.RefreshFeed.Success(id))
            }
            .onFailure { reason ->
                _events.emit(Event.RefreshFeed.Failure(reason))
            }
    }

    private suspend fun addFeed(
        feedUrl: URL,
        categoryId: Int,
    ): Result<Int> {
        return addFeed.perform(feedUrl, categoryId)
            .onSuccess { feedId ->
                _events.emit(Event.AddFeed.Success(feedId))
            }
            .onFailure { reason ->
                _events.emit(Event.AddFeed.Failure(reason))
            }
    }

    private suspend fun editFeed(
        id: Int,
        title: String,
        feedUrl: URL,
        categoryId: Int,
    ): Result<Feed> {
        return editFeed.perform(id, title, feedUrl, categoryId)
            .onSuccess { feed ->
                _feedsById.update { it + (feed.id to feed) }
                _events.emit(Event.EditFeed.Success(feed.id))
            }
            .onFailure { reason ->
                _events.emit(Event.EditFeed.Failure(reason))
            }
    }

    private suspend fun removeFeed(
        id: Int,
    ): Result<Unit> {
        return removeFeed.perform(id)
            .onSuccess {
                _feedsById.update { it - id }
                _events.emit(Event.RemoveFeed.Success(id))
            }
            .onFailure { reason ->
                _events.emit(Event.RemoveFeed.Failure(reason))
            }
    }

    private suspend fun reloadAllEntries(): Result<Pair<List<Entry>, Int>> {
        _entriesStatus.value = Status.Loading

        val entriesPerPage = preferenceStore.entriesPerPage.first()

        return getEntries.perform(_source.value, offset.value, entriesPerPage)
            .onSuccess { (entries, total) ->
                _entriesById.value = entries.associateBy { it.id }
                _totalEntries.value = total
                _entriesStatus.value = Status.Success
                _events.emit(Event.LoadEntries.Success)
            }
            .onFailure { reason ->
                _entriesStatus.value = Status.Failure(reason)
                _events.emit(Event.LoadEntries.Failure(reason))
            }
    }

    private suspend fun markAllEntriesAsRead(): Result<Unit> {
        val entryIds = _entriesById.value.keys.toList()

        return markEntriesAsRead.perform(entryIds)
            .onSuccess {
                _events.emit(Event.MarkAllEntriesAsRead.Success)
            }
            .onFailure { reason ->
                _events.emit(Event.MarkAllEntriesAsRead.Failure(reason))
            }
    }
}
