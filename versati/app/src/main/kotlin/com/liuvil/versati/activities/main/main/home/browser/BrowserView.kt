package com.liuvil.versati.activities.main.main.home.browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.main.home.browser.category.add.AddCategoryDialog
import com.liuvil.versati.activities.main.main.home.browser.category.edit.EditCategoryDialog
import com.liuvil.versati.activities.main.main.home.browser.category.remove.RemoveCategoryDialog
import com.liuvil.versati.activities.main.main.home.browser.entry_tile.EntryTile
import com.liuvil.versati.activities.main.main.home.browser.page.PageDialog
import com.liuvil.versati.activities.main.main.home.browser.search.SearchDialog
import com.liuvil.versati.activities.main.main.home.browser.feed.add.AddFeedDialog
import com.liuvil.versati.activities.main.main.home.browser.feed.add.Category
import com.liuvil.versati.activities.main.main.home.browser.feed.edit.EditFeedDialog
import com.liuvil.versati.activities.main.main.home.browser.feed.remove.RemoveFeedDialog
import com.liuvil.versati.components.BlockingBox
import com.liuvil.versati.components.BlockingDialog
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.components.ErrorDialog
import com.liuvil.versati.components.drawer.DrawerItem
import com.liuvil.versati.components.drawer.DrawerItemGroup
import com.liuvil.versati.components.drawer.DrawerItemIcon
import com.liuvil.versati.components.drawer.DrawerItemLabel
import com.liuvil.versati.components.drawer.DrawerSectionHeader
import com.liuvil.versati.components.drawer.DrawerSectionHeaderButton
import com.liuvil.versati.components.sheet.ActionBottomSheet
import com.liuvil.versati.components.sheet.ActionBottomSheetHeader
import com.liuvil.versati.components.sheet.ActionBottomSheetItem
import com.liuvil.versati.framework.date.formatHumanReadable
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.throwable.detailedMessage
import com.liuvil.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.math.ceil
import kotlin.math.max

// TODO: Make configurable
const val PAGE_ENTRY_COUNT = 10

private sealed class Modal

private sealed class Menu: Modal() {
    data object CategoriesAndFeeds: Menu()

    data class Category(
        val id: Int
    ): Menu()

    data class Feed(
        val id: Int
    ): Menu()
}

private sealed class Dialog: Modal() {
    sealed class AddCategory {
        data object Input: Dialog()
        data object Loading: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    sealed class EditCategory {
        data class Input(val id: Int): Dialog()
        data object Loading: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    sealed class RemoveCategory {
        data class Confirmation(val id: Int): Dialog()
        data object Loading: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    sealed class AddFeed {
        data object Input: Dialog()
        data object Loading: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    sealed class EditFeed {
        data class Input(val id: Int): Dialog()
        data object Loading: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    sealed class RemoveFeed {
        data class Confirmation(val id: Int): Dialog()
        data object Loading: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    data class SearchEntries(
        val initialTerm: String
    ): Dialog()

    data class GoToPage(
        val currentPage: Int,
        val totalPages: Int
    ): Dialog()

    sealed class MarkEntriesAsRead {
        data class Confirmation(val entryIds: List<Int>): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }
}

private sealed class Snackbar {
    data object AddedCategory: Snackbar()
    data object EditedCategory: Snackbar()
    data object RemovedCategory: Snackbar()
    data object AddedFeed: Snackbar()
    data object EditedFeed: Snackbar()
    data object RemovedFeed: Snackbar()
}

sealed interface Source {
    data object Unread: Source
    data object History: Source
    data object Starred: Source
    data class Category(val id: Int): Source
    data class Feed(val id: Int): Source
    data class Search(val term: String): Source
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserView(
    onEntryClicked: (Int) -> Unit,
    onPreferencesClicked: () -> Unit
) = viewOf<BrowserViewModel> { viewModel ->
    var source by viewModel.source
    var offset by viewModel.offset
    val categories by viewModel.categories
    val feeds by viewModel.feeds
    val iconsById = viewModel.iconsById
    val entries by viewModel.entries
    val totalEntries by viewModel.totalEntries

    val categoriesById by remember {
        derivedStateOf {
            categories.map { categories ->
                categories.associateBy { it.id }
            }
        }
    }

    val feedsById by remember {
        derivedStateOf {
            feeds.map { feeds ->
                feeds.associateBy { it.id }
            }
        }
    }

    // Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val expandedCategories = remember { mutableStateMapOf<Int, Boolean>() }

    // Entry List View
    val isRefreshing by remember {
        derivedStateOf { entries is None || entries is Loading }
    }

    // Dialogs and Menus
    var activeModal by remember { mutableStateOf<Modal?>(null) }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        categories.ifNone {
            viewModel.reloadCategories()
        }

        feeds.ifNone {
            viewModel.reloadFeeds()
        }

        feeds.ifSuccess { feeds ->
            feeds.forEach { feed ->
                viewModel.reloadIcon(feed.iconId)
            }
        }

        entries.ifNone {
            viewModel.reloadEntries()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RectangleShape
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        DrawerItem(
                            label = {
                                DrawerItemLabel("Unread")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.Today)
                            },
                            selected = source == Source.Unread,
                            onClick = {
                                source = Source.Unread
                                offset = 0

                                coroutineScope.launch {
                                    drawerState.close()
                                    viewModel.reloadEntries()
                                }
                            }
                        )

                        DrawerItem(
                            label = {
                                DrawerItemLabel("Starred")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.StarOutline)
                            },
                            selected = source == Source.Starred,
                            onClick = {
                                source = Source.Starred
                                offset = 0

                                coroutineScope.launch {
                                    drawerState.close()
                                    viewModel.reloadEntries()
                                }
                            }
                        )

                        DrawerSectionHeader(
                            title = "Categories and feeds",
                            buttons = {
                                DrawerSectionHeaderButton(
                                    icon = Icons.Default.MoreHoriz,
                                    onClick = {
                                        activeModal = Menu.CategoriesAndFeeds
                                    }
                                )
                            }
                        )

                        categories.ifSuccess { categories ->
                            feeds.ifSuccess { feeds ->
                                val feedsByCategoryId = feeds.groupBy { it.categoryId }

                                categories.forEach { category ->
                                    val expanded = expandedCategories.getOrDefault(category.id, false)
                                    DrawerItemGroup(
                                        label = {
                                            DrawerItemLabel(category.title)
                                        },
                                        expanded = expanded,
                                        selected = source == Source.Category(category.id),
                                        onClick = {
                                            source = Source.Category(category.id)
                                            offset = 0

                                            coroutineScope.launch {
                                                drawerState.close()
                                                viewModel.reloadEntries()
                                            }
                                        },
                                        onLongClick = {
                                            activeModal = Menu.Category(category.id)
                                        },
                                        onToggle = {
                                            if (expanded) {
                                                expandedCategories.remove(category.id)
                                            } else {
                                                expandedCategories[category.id] = true
                                            }
                                        }
                                    ) {
                                        feedsByCategoryId.getOrDefault(category.id, emptyList()).forEach { feed ->
                                            DrawerItem(
                                                label = {
                                                    DrawerItemLabel(feed.title)
                                                },
                                                icon = {
                                                    iconsById[feed.iconId]?.let { icon ->
                                                        icon.ifSuccess {
                                                            DrawerItemIcon(it)
                                                        }
                                                    } ?: Text("${iconsById.size}")
                                                },
                                                selected = source == Source.Feed(feed.id),
                                                onClick = {
                                                    source = Source.Feed(feed.id)
                                                    offset = 0

                                                    coroutineScope.launch {
                                                        drawerState.close()
                                                        viewModel.reloadEntries()
                                                    }
                                                },
                                                onLongClick = {
                                                    activeModal = Menu.Feed(feed.id)
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.size(4.dp))

                        DrawerItem(
                            label = {
                                DrawerItemLabel("History")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.History)
                            },
                            selected = source == Source.History,
                            onClick = {
                                source = Source.History
                                offset = 0

                                coroutineScope.launch {
                                    drawerState.close()
                                    viewModel.reloadEntries()
                                }
                            }
                        )

                        DrawerItem(
                            label = {
                                DrawerItemLabel("Search")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.Search)
                            },
                            selected = source is Source.Search,
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                    activeModal = Dialog.SearchEntries(
                                        initialTerm = source.let {
                                            if (it is Source.Search) {
                                                it.term
                                            } else {
                                                ""
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            val title = source.let { source ->
                                when (source) {
                                    is Source.Unread -> "Unread"
                                    is Source.History -> "History"
                                    is Source.Starred -> "Starred"
                                    is Source.Category ->
                                        categoriesById.ifSuccess { categoriesById ->
                                            categoriesById[source.id]?.title
                                        }
                                    is Source.Feed ->
                                        feedsById.ifSuccess { feedsById ->
                                            feedsById[source.id]?.title
                                        }
                                    is Source.Search -> "Search '${source.term}'"
                                }
                            }
                            title?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = {
                                                source.let { source ->
                                                    when (source) {
                                                        is Source.Category ->
                                                            activeModal = Menu.Category(
                                                                id = source.id
                                                            )
                                                        is Source.Feed ->
                                                            activeModal = Menu.Feed(
                                                                id = source.id
                                                            )
                                                        is Source.Search ->
                                                            activeModal = Dialog.SearchEntries(
                                                                initialTerm = source.term
                                                            )
                                                        else -> {}
                                                    }
                                                }
                                            },
                                            interactionSource = null,
                                            indication = null
                                        )
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    onPreferencesClicked()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) { padding ->
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        coroutineScope.launch {
                            viewModel.reloadEntries()
                        }
                    },
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    BlockingBox(
                        isBlocking = isRefreshing
                    ) {
                        entries.apply {
                            ifSuccess { entries ->
                                if (entries.isNotEmpty()) {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {
                                            entries
                                                .groupBy {
                                                    it.publishedAt
                                                        .atZoneSameInstant(ZoneId.systemDefault())
                                                        .toLocalDate()
                                                }
                                                .forEach { (date, entries) ->
                                                    Text(
                                                        text = date.formatHumanReadable(),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                start = 10.dp,
                                                                top = 10.dp,
                                                                end = 10.dp
                                                            )
                                                    )

                                                    Column (
                                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                    ) {
                                                        entries.forEach { entry ->
                                                            EntryTile(
                                                                title = entry.title,
                                                                feedTitle = feedsById.ifSuccess {
                                                                    it[entry.feedID]?.title
                                                                } ?: "...",
                                                                timeSincePublished = Duration.between(
                                                                    entry.publishedAt,
                                                                    OffsetDateTime.now()
                                                                ),
                                                                content = entry.text,
                                                                imageURL = entry.imageURL,
                                                                isRead = entry.isRead,
                                                                onClick = {
                                                                    onEntryClicked(entry.id)
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                        }

                                        item {
                                            Row(Modifier.padding(12.dp)) {
                                                if (entries.any { !it.isRead }) {
                                                    Button(
                                                        onClick = {
                                                            activeModal = Dialog.MarkEntriesAsRead.Confirmation(
                                                                entryIds = entries.map {
                                                                    it.id
                                                                }
                                                            )
                                                        }
                                                    ) {
                                                        Text("Mark all as read")
                                                    }
                                                }

                                                Spacer(Modifier.weight(1f))

                                                if (offset > 0) {
                                                    IconButton(
                                                        onClick = {
                                                            coroutineScope.launch {
                                                                offset = max(
                                                                    offset - PAGE_ENTRY_COUNT,
                                                                    0
                                                                )

                                                                coroutineScope.launch {
                                                                    viewModel.reloadEntries()
                                                                }
                                                            }
                                                        },
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }

                                                totalEntries.ifSuccess { totalEntries ->
                                                    val currentPage = offset / PAGE_ENTRY_COUNT + 1
                                                    val totalPages = ceil(totalEntries.toFloat() / PAGE_ENTRY_COUNT).toInt()

                                                    TextButton(
                                                        onClick = {
                                                            activeModal = Dialog.GoToPage(
                                                                currentPage = currentPage,
                                                                totalPages = totalPages
                                                            )
                                                        }
                                                    ) {
                                                        Text("$currentPage / $totalPages")
                                                    }

                                                    if (offset < totalEntries - PAGE_ENTRY_COUNT) {
                                                        IconButton(
                                                            onClick = {
                                                                offset += PAGE_ENTRY_COUNT

                                                                coroutineScope.launch {
                                                                    viewModel.reloadEntries()
                                                                }
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Text("No entries found.")
                                    }
                                }
                            }

                            ifFailure { exception ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Failed to load entries",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = exception.detailedMessage,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    activeModal?.let { modal ->
        when (modal) {
            is Menu.CategoriesAndFeeds ->
                ActionBottomSheet(
                    onDismiss = {
                        activeModal = null
                    }
                ) {
                    ActionBottomSheetItem(
                        title = "Reload categories and feeds",
                        icon = Icons.Default.Refresh
                    ) {
                        activeModal = null

                        coroutineScope.launch {
                            viewModel.reloadCategories()
                            viewModel.reloadFeeds()
                        }
                    }

                    ActionBottomSheetItem(
                        title = "Add a category",
                        icon = Icons.Default.Add
                    ) {
                        activeModal = Dialog.AddCategory.Input
                    }

                    ActionBottomSheetItem(
                        title = "Add a feed",
                        icon = Icons.Default.Add
                    ) {
                        activeModal = Dialog.AddFeed.Input
                    }
                }

            is Menu.Category ->
                categoriesById
                    .ifSuccess { categoriesById ->
                        categoriesById[modal.id]?.let { category ->
                            ActionBottomSheet(
                                onDismiss = {
                                    activeModal = null
                                }
                            ) {
                                ActionBottomSheetHeader(category.title)

                                ActionBottomSheetItem(
                                    title = "Edit category",
                                    icon = Icons.Default.Edit
                                ) {
                                    activeModal = Dialog.EditCategory.Input(
                                        id = modal.id
                                    )
                                }

                                ActionBottomSheetItem(
                                    title = "Remove category",
                                    icon = Icons.Default.Delete,
                                    destructive = true
                                ) {
                                    activeModal = Dialog.RemoveCategory.Confirmation(
                                        id = modal.id
                                    )
                                }
                            }
                        }
                    }

            is Menu.Feed ->
                feedsById.ifSuccess { feedsById ->
                    feedsById[modal.id]?.let { feed ->
                        ActionBottomSheet(
                            onDismiss = {
                                activeModal = null
                            }
                        ) {
                            ActionBottomSheetHeader(feed.title)

                            ActionBottomSheetItem(
                                title = "Edit feed",
                                icon = Icons.Default.Edit
                            ) {
                                activeModal = Dialog.EditFeed.Input(
                                    id = modal.id
                                )
                            }

                            ActionBottomSheetItem(
                                title = "Remove feed",
                                icon = Icons.Default.Delete,
                                destructive = true
                            ) {
                                activeModal = Dialog.RemoveFeed.Confirmation(
                                    id = modal.id
                                )
                            }
                        }
                    }
                }

            is Dialog.AddCategory.Input ->
                AddCategoryDialog(
                    onSubmit = {
                        coroutineScope.launch {
                            activeModal = Dialog.AddCategory.Loading

                            val categoryId = try {
                                viewModel.createCategory(
                                    title = it.title
                                )
                            } catch (reason: Throwable) {
                                activeModal = Dialog.AddCategory.Failure(reason)
                                return@launch
                            }

                            activeModal = null

                            viewModel.reloadCategories()

                            source = Source.Category(id = categoryId)
                            offset = 0
                            viewModel.reloadEntries()

                            snackbarHostState.showSnackbar(
                                message = getSnackbarMessage(Snackbar.AddedCategory)
                            )
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.AddCategory.Loading ->
                BlockingDialog {
                    CircularProgressIndicator()
                }

            is Dialog.AddCategory.Failure ->
                ErrorDialog(
                    titleText = "Failed to add category",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.EditCategory.Input ->
                categoriesById.ifSuccess { categoriesById ->
                    categoriesById[modal.id]?.let { category ->
                        EditCategoryDialog(
                            initialTitle = category.title,
                            onSubmit = {
                                activeModal = Dialog.EditCategory.Loading

                                coroutineScope.launch {
                                    try {
                                        viewModel.updateCategory(
                                            id = modal.id,
                                            title = it.title
                                        )
                                    } catch (reason: Throwable) {
                                        activeModal = Dialog.EditCategory.Failure(reason)
                                        return@launch
                                    }

                                    activeModal = null

                                    viewModel.reloadCategories()
                                    viewModel.reloadEntries()
                                    snackbarHostState.showSnackbar(
                                        message = getSnackbarMessage(Snackbar.EditedCategory)
                                    )
                                }
                            },
                            onRespond = {
                                activeModal = null
                            }
                        )
                    }
                }

            is Dialog.EditCategory.Loading ->
                BlockingDialog {
                    CircularProgressIndicator()
                }

            is Dialog.EditCategory.Failure ->
                ErrorDialog(
                    titleText = "Failed to edit category",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.RemoveCategory.Confirmation ->
                categoriesById.ifSuccess { categoriesById ->
                    categoriesById[modal.id]?.let { category ->
                        RemoveCategoryDialog(
                            title = category.title,
                            onConfirm = {
                                activeModal = Dialog.RemoveCategory.Loading

                                coroutineScope.launch {
                                    try {
                                        viewModel.deleteCategory(
                                            id = modal.id
                                        )
                                    } catch (reason: Throwable) {
                                        activeModal = Dialog.RemoveCategory.Failure(reason)
                                        return@launch
                                    }

                                    activeModal = null

                                    viewModel.reloadCategories()
                                    viewModel.reloadFeeds()
                                    viewModel.reloadEntries()

                                    snackbarHostState.showSnackbar(
                                        message = getSnackbarMessage(Snackbar.RemovedCategory)
                                    )
                                }
                            },
                            onRespond = {
                                activeModal = null
                            }
                        )
                    }
                }

            is Dialog.RemoveCategory.Loading ->
                BlockingDialog {
                    CircularProgressIndicator()
                }

            is Dialog.RemoveCategory.Failure ->
                ErrorDialog(
                    titleText = "Failed to remove category",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.AddFeed.Input ->
                categories.ifSuccess { categories ->
                    AddFeedDialog(
                        categories = categories.map {
                            Category(
                                id = it.id,
                                title = it.title
                            )
                        },
                        onSubmit = {
                            activeModal = Dialog.AddFeed.Loading

                            coroutineScope.launch {
                                val feedId = try {
                                    viewModel.createFeed(
                                        feedUrl = it.feedUrl,
                                        categoryId = it.categoryId
                                    )
                                } catch (reason: Throwable) {
                                    activeModal = Dialog.AddFeed.Failure(reason)
                                    return@launch
                                }

                                activeModal = null

                                viewModel.reloadFeeds()

                                feedsById.ifSuccess { feedsById ->
                                    feedsById[feedId]?.let { feed ->
                                        viewModel.reloadIcon(id = feed.iconId)
                                    }
                                }

                                source = Source.Feed(id = feedId)
                                offset = 0
                                viewModel.reloadEntries()

                                snackbarHostState.showSnackbar(
                                    message = getSnackbarMessage(Snackbar.AddedFeed)
                                )
                            }
                        },
                        onRespond = {
                            activeModal = null
                        }
                    )
                }

            is Dialog.AddFeed.Loading ->
                BlockingDialog {
                    CircularProgressIndicator()
                }

            is Dialog.AddFeed.Failure ->
                ErrorDialog(
                    titleText = "Failed to add feed",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.EditFeed.Input ->
                categories.ifSuccess { categories ->
                    feedsById.ifSuccess { feedsById ->
                        feedsById[modal.id]?.let { feed ->
                            EditFeedDialog(
                                initialTitle = feed.title,
                                initialFeedUrl = feed.feedUrl,
                                initialCategoryId = feed.categoryId,
                                categories = categories.map {
                                    com.liuvil.versati.activities.main.main.home.browser.feed.edit.Category(
                                        id = it.id,
                                        title = it.title
                                    )
                                },
                                onSubmit = {
                                    activeModal = Dialog.EditFeed.Loading

                                    coroutineScope.launch {
                                        try {
                                            viewModel.updateFeed(
                                                id = modal.id,
                                                title = it.title,
                                                feedUrl = it.feedUrl,
                                                categoryId = it.categoryId
                                            )
                                        } catch (reason: Throwable) {
                                            activeModal = Dialog.EditFeed.Failure(reason)
                                            return@launch
                                        }

                                        activeModal = null

                                        viewModel.reloadFeeds()
                                        viewModel.reloadEntries()
                                        snackbarHostState.showSnackbar(
                                            message = getSnackbarMessage(Snackbar.EditedFeed)
                                        )
                                    }
                                },
                                onRespond = {
                                    activeModal = null
                                }
                            )
                        }
                    }
                }

            is Dialog.EditFeed.Loading ->
                BlockingDialog {
                    CircularProgressIndicator()
                }

            is Dialog.EditFeed.Failure ->
                ErrorDialog(
                    titleText = "Failed to edit feed",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.RemoveFeed.Confirmation ->
                feedsById.ifSuccess { feedsById ->
                    feedsById[modal.id]?.let { feed ->
                        RemoveFeedDialog(
                            title = feed.title,
                            onConfirm = {
                                activeModal = Dialog.RemoveFeed.Loading

                                coroutineScope.launch {
                                    try {
                                        viewModel.deleteFeed(
                                            feedId = modal.id
                                        )
                                    } catch (reason: Throwable) {
                                        activeModal = Dialog.RemoveFeed.Failure(reason)
                                        return@launch
                                    }

                                    activeModal = null

                                    viewModel.reloadFeeds()
                                    viewModel.reloadEntries()
                                    snackbarHostState.showSnackbar(
                                        message = getSnackbarMessage(Snackbar.RemovedFeed)
                                    )
                                }
                            },
                            onRespond = {
                                activeModal = null
                            }
                        )
                    }
                }

            is Dialog.RemoveFeed.Loading ->
                BlockingDialog {
                    CircularProgressIndicator()
                }

            is Dialog.RemoveFeed.Failure ->
                ErrorDialog(
                    titleText = "Failed to remove feed",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.SearchEntries ->
                SearchDialog(
                    initialTerm = modal.initialTerm,
                    onSubmit = { searchTerm ->
                        source = Source.Search(searchTerm)
                        offset = 0

                        coroutineScope.launch {
                            drawerState.close()
                            viewModel.reloadEntries()
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.GoToPage ->
                PageDialog(
                    initialValue = modal.currentPage,
                    totalPages = modal.totalPages,
                    onSubmit = {
                        offset = (it - 1) * PAGE_ENTRY_COUNT

                        coroutineScope.launch {
                            viewModel.reloadEntries()
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.MarkEntriesAsRead.Confirmation ->
                ConfirmationDialog(
                    titleText = "Mark page as read",
                    bodyText = "Are you sure you want to mark all entries in this page as read?",
                    confirmText = "Mark as read",
                    dismissText = "Cancel",
                    onConfirm = {
                        coroutineScope.launch {
                            try {
                                viewModel.markAsRead(
                                    entryIds = modal.entryIds
                                )
                            } catch (reason: Throwable) {
                                activeModal = Dialog.MarkEntriesAsRead.Failure(reason)
                                return@launch
                            }

                            coroutineScope.launch {
                                viewModel.reloadEntries()
                            }
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.MarkEntriesAsRead.Failure ->
                ErrorDialog(
                    titleText = "Failed to mark entries as read",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )
        }
    }
}

private fun getSnackbarMessage(
    snackbar: Snackbar
) =
    when (snackbar) {
        is Snackbar.AddedCategory ->
            "Category successfully added"

        is Snackbar.EditedCategory ->
            "Category successfully edited"

        is Snackbar.RemovedCategory ->
            "Category successfully removed"

        is Snackbar.AddedFeed ->
            "Feed successfully added"

        is Snackbar.EditedFeed ->
            "Feed successfully edited"

        is Snackbar.RemovedFeed ->
            "Feed successfully removed"
    }
