package com.liuvil.versati.activities.main.main.home.entry.browse

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.main.home.entry.browse.entry_tile.EntryTile
import com.liuvil.versati.activities.main.main.home.entry.browse.page.PageDialog
import com.liuvil.versati.activities.main.main.home.entry.browse.search.SearchDialog
import com.liuvil.versati.components.BlockingBox
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.components.drawer.DrawerItem
import com.liuvil.versati.components.drawer.DrawerItemGroup
import com.liuvil.versati.components.drawer.DrawerItemIcon
import com.liuvil.versati.components.drawer.DrawerItemLabel
import com.liuvil.versati.components.drawer.DrawerSectionHeader
import com.liuvil.versati.components.drawer.DrawerSectionHeaderButton
import com.liuvil.versati.framework.date.formatHumanReadable
import com.liuvil.versati.framework.throwable.detailedMessage
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
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
    data class Search(
        val initialTerm: String
    ): Dialog()

    data class Page(
        val currentPage: Int,
        val totalPages: Int
    ): Dialog()

    data class MarkAsReadConfirmation(
        val entryIds: List<Int>
    ): Dialog()
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
fun BrowseEntriesView(
    onEntryClicked: (Int) -> Unit,
    onAddCategoryClicked: () -> Unit,
    onEditCategoryClicked: (Int) -> Unit,
    onRemoveCategoryClicked: (Int) -> Unit,
    onAddFeedClicked: () -> Unit,
    onEditFeedClicked: (Int) -> Unit,
    onRemoveFeedClicked: (Int) -> Unit,
    onPreferencesClicked: () -> Unit
) = viewOf<BrowseEntriesViewModel> { viewModel ->
    var source by viewModel.source
    var offset by viewModel.offset
    val categories by viewModel.categories
    val feeds by viewModel.feeds
    val feedCounters by viewModel.feedCounters
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
    val scrollState = rememberLazyListState()
    val isRefreshing by remember {
        derivedStateOf { entries is None || entries is Loading }
    }

    // Dialogs
    var activeModal by remember { mutableStateOf<Modal?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val updateSourceSelection: suspend (Source) -> Unit = remember {
        {
            source = it
            offset = 0
            drawerState.close()
            coroutineScope.launch {
                viewModel.reloadEntries()
            }
            scrollState.scrollToItem(0)
        }
    }

    val updateOffset: suspend (Int) -> Unit = remember {
        {
            offset = it
            coroutineScope.launch {
                viewModel.reloadEntries()
            }
            scrollState.scrollToItem(0)
        }
    }

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
                    val totalUnreadCount = feedCounters.ifSuccess { feedCounters ->
                        feedCounters.unreads.values.sum()
                    }

                    DrawerItem(
                        label = {
                            DrawerItemLabel("Unread")
                        },
                        icon = {
                            DrawerItemIcon(Icons.Outlined.Today)
                        },
                        badge = {
                            totalUnreadCount
                                ?.takeIf { it > 0 }
                                ?.let {
                                    Text("$it")
                                }
                        },
                        selected = source == Source.Unread,
                        onClick = {
                            coroutineScope.launch {
                                updateSourceSelection(Source.Unread)
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
                            coroutineScope.launch {
                                updateSourceSelection(Source.Starred)
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
                                val categoryUnreadCount = feedCounters.ifSuccess { feedCounters ->
                                    feedsByCategoryId.getOrDefault(category.id, emptyList())
                                        .sumOf { feed ->
                                            feedCounters.unreads.getOrDefault(feed.id, 0)
                                        }
                                }

                                val expanded = expandedCategories.getOrDefault(category.id, false)
                                DrawerItemGroup(
                                    label = {
                                        DrawerItemLabel(category.title)
                                    },
                                    expanded = expanded,
                                    selected = source == Source.Category(category.id),
                                    badge = {
                                        categoryUnreadCount?.let {
                                            Text("$it")
                                        }
                                    },
                                    onClick = {
                                        coroutineScope.launch {
                                            updateSourceSelection(Source.Category(category.id))
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
                                        val feedUnreadCount = feedCounters.ifSuccess { feedCounters ->
                                            feedCounters.unreads.getOrDefault(feed.id, 0)
                                        }
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
                                            badge = {
                                                feedUnreadCount?.let {
                                                    Text("$it")
                                                }
                                            },
                                            selected = source == Source.Feed(feed.id),
                                            onClick = {
                                                coroutineScope.launch {
                                                    updateSourceSelection(Source.Feed(feed.id))
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

                    val totalReadCount = feedCounters.ifSuccess { feedCounters ->
                        feedCounters.reads.values.sum()
                    }
                    DrawerItem(
                        label = {
                            DrawerItemLabel("History")
                        },
                        icon = {
                            DrawerItemIcon(Icons.Outlined.History)
                        },
                        badge = {
                            totalReadCount
                                ?.takeIf { it > 0 }
                                ?.let {
                                    Text("$it")
                                }
                        },
                        selected = source == Source.History,
                        onClick = {
                            coroutineScope.launch {
                                updateSourceSelection(Source.History)
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
                                activeModal = Dialog.Search(
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
                                Source.Unread -> "Unread"
                                Source.History -> "History"
                                Source.Starred -> "Starred"
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
                                                        activeModal = Dialog.Search(
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
                                    state = scrollState,
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
                                                        activeModal = Dialog.MarkAsReadConfirmation(
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
                                                            updateOffset(
                                                                max(
                                                                    offset - PAGE_ENTRY_COUNT,
                                                                    0
                                                                )
                                                            )
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
                                                        activeModal = Dialog.Page(
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
                                                            coroutineScope.launch {
                                                                updateOffset(offset + PAGE_ENTRY_COUNT)
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
                                        text = exception.detailedMessage ?: "Unknown error",
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

    activeModal?.let {
        when (it) {
            is Menu.CategoriesAndFeeds ->
                ActionBottomSheet(
                    title = "Categories and feeds",
                    items = listOf(
                        ActionBottomSheetItem(
                            title = "Reload",
                            icon = Icons.Default.Refresh,
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.reloadCategories()
                                    viewModel.reloadFeeds()
                                }
                            }
                        ),
                        ActionBottomSheetItem(
                            title = "Add a category",
                            icon = Icons.Default.Add,
                            onClick = {
                                onAddCategoryClicked()
                            }
                        ),
                        ActionBottomSheetItem(
                            title = "Add a feed",
                            icon = Icons.Default.Add,
                            onClick = {
                                onAddFeedClicked()
                            }
                        )
                    ),
                    onDismiss = {
                        activeModal = null
                    }
                )

            is Menu.Category ->
                ActionBottomSheet(
                    title = categoriesById
                        .ifSuccess { categoriesById ->
                            categoriesById[it.id]?.title
                        },
                    items = listOf(
                        ActionBottomSheetItem(
                            title = "Edit",
                            icon = Icons.Default.Edit,
                            onClick = {
                                onEditCategoryClicked(it.id)
                            }
                        ),
                        ActionBottomSheetItem(
                            title = "Remove",
                            icon = Icons.Default.Delete,
                            destructive = true,
                            onClick = {
                                onRemoveCategoryClicked(it.id)
                            }
                        )
                    ),
                    onDismiss = {
                        activeModal = null
                    }
                )

            is Menu.Feed ->
                ActionBottomSheet(
                    title = feedsById
                        .ifSuccess { feedsById ->
                            feedsById[it.id]?.title
                        },
                    items = listOf(
                        ActionBottomSheetItem(
                            title = "Edit",
                            icon = Icons.Default.Edit,
                            onClick = {
                                onEditFeedClicked(it.id)
                            }
                        ),
                        ActionBottomSheetItem(
                            title = "Remove",
                            icon = Icons.Default.Delete,
                            destructive = true,
                            onClick = {
                                onRemoveFeedClicked(it.id)
                            }
                        )
                    ),
                    onDismiss = {
                        activeModal = null
                    }
                )

            is Dialog.Search ->
                SearchDialog(
                    initialTerm = it.initialTerm,
                    onSubmit = { searchTerm ->
                        coroutineScope.launch {
                            updateSourceSelection(
                                Source.Search(
                                    term = searchTerm
                                )
                            )
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.Page ->
                PageDialog(
                    initialValue = it.currentPage,
                    totalPages = it.totalPages,
                    onSubmit = {
                        coroutineScope.launch {
                            updateOffset((it - 1) * PAGE_ENTRY_COUNT)
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.MarkAsReadConfirmation ->
                ConfirmationDialog(
                    titleText = "Mark page as read",
                    bodyText = "Are you sure you want to mark all entries in this page as read?",
                    confirmText = "Mark as read",
                    dismissText = "Cancel",
                    onConfirm = {
                        coroutineScope.launch {
                            viewModel.markAsRead(
                                entryIds = it.entryIds
                            )
                            coroutineScope.launch {
                                viewModel.reloadEntries()
                            }
                            scrollState.scrollToItem(0)
                        }
                    },
                    onRespond = {
                        activeModal = null
                    }
                )
        }
    }
}

data class ActionBottomSheetItem(
    val title: String,
    val icon: ImageVector,
    val destructive: Boolean = false,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    title: String? = null,
    items: List<ActionBottomSheetItem>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items.forEach {
                ActionItem(
                    text = it.title,
                    icon = it.icon,
                    destructive = it.destructive,
                    onClick = {
                        it.onClick()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun ActionItem(
    text: String,
    icon: ImageVector,
    destructive: Boolean,
    onClick: () -> Unit
) {
    val tint =
        if (destructive) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            color = tint
        )
    }
}
