package com.liuvil.versati.activities.main.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Book
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.feed.drawer.Drawer
import com.liuvil.versati.activities.main.feed.drawer.ExpandableDrawerItem
import com.liuvil.versati.activities.main.feed.drawer.FlatDrawerItem
import com.liuvil.versati.activities.main.feed.entry_list.EntryListView
import com.liuvil.versati.activities.main.feed.entry_list.buildFromAPIModel
import com.liuvil.versati.activities.main.feed.page.PageDialog
import com.liuvil.versati.activities.main.feed.search.SearchDialog
import com.liuvil.versati.api.data.EntryStatus
import com.liuvil.versati.components.BlockingBox
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.framework.date.formatHumanReadable
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch
import java.time.ZoneId
import kotlin.math.ceil
import kotlin.math.max

// TODO: Make configurable
const val PAGE_ENTRY_COUNT = 10

sealed interface Source {
    data object Unread: Source
    data object Read: Source
    data object Starred: Source
    data class Category(val id: Int): Source
    data class Feed(val id: Int): Source
    data class Search(val term: String): Source
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedView(
    onEntryOpenRequest: (Int) -> Unit
) {
    val viewModel = bindViewModel<FeedViewModel>()
    var source by viewModel.source
    var offset by viewModel.offset
    val categories by viewModel.categories
    val feeds by viewModel.feeds
    val feedIconsById = viewModel.feedIconsById
    val feedCounters by viewModel.feedCounters
    val entriesResponse by viewModel.entriesResponse

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

    val areThereUnreadEntries by remember {
        derivedStateOf {
            entriesResponse.map {
                it.entries.any { it.status == EntryStatus.UNREAD }
            }
        }
    }

    // Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val expandedCategories = remember { mutableStateMapOf<Int, Boolean>() }

    // Entry List View
    val scrollState = rememberLazyListState()
    var showMarkAsReadConfirmationDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showPageDialog by remember { mutableStateOf(false) }
    val isRefreshing by remember {
        derivedStateOf { entriesResponse is None || entriesResponse is Loading }
    }

    val coroutineScope = rememberCoroutineScope()

    val updateSourceSelection: suspend (Source) -> Unit = remember {
        {
            source = it
            offset = 0
            drawerState.close()
            viewModel.reloadEntries()
            scrollState.scrollToItem(0)
        }
    }

    val updateOffset: suspend (Int) -> Unit = remember {
        {
            offset = it
            viewModel.reloadEntries()
            scrollState.scrollToItem(0)
        }
    }

    LaunchedEffect(Unit) {
        categories.ifNone {
            viewModel.reloadCategories()
        }

        feeds.ifNone {
            viewModel.reloadFeeds()

            feeds.ifSuccess { feeds ->
                feeds.forEach { feed ->
                    viewModel.reloadFeedIcon(feed.icon.iconId)
                }
            }

            viewModel.reloadFeedCounters()
        }

        entriesResponse.ifNone {
            viewModel.reloadEntries()
        }
    }

    Drawer(
        items = buildList {
            val totalUnreadCount = feedCounters.ifSuccess { feedCounters ->
                feedCounters.unreads.values.sum()
            }

            add(
                FlatDrawerItem(
                    title = "Unread",
                    icon = FlatDrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Today
                    ),
                    badge = totalUnreadCount?.let { "$it" },
                    selected = source == Source.Unread
                ) {
                    coroutineScope.launch {
                        updateSourceSelection(Source.Unread)
                    }
                }
            )

            add(
                FlatDrawerItem(
                    title = "Starred",
                    icon = FlatDrawerItem.Icon.Vector(
                        vector = Icons.Outlined.StarOutline
                    ),
                    selected = source == Source.Starred
                ) {
                    coroutineScope.launch {
                        updateSourceSelection(Source.Starred)
                    }
                }
            )

            categories.ifSuccess { categories ->
                feeds.ifSuccess { feeds ->
                    val feedsByCategoryId = feeds.groupBy { it.category.id }

                    categories.forEach { category ->
                        val categoryUnreadCount = feedCounters.ifSuccess { feedCounters ->
                            feedsByCategoryId.getOrDefault(category.id, emptyList())
                                .sumOf { feed ->
                                    feedCounters.unreads.getOrDefault(feed.id, 0)
                                }
                        }

                        add(
                            ExpandableDrawerItem(
                                title = category.title,
                                badge = categoryUnreadCount?.let { "$it" },
                                selected = source == Source.Category(category.id),
                                expanded = expandedCategories.getOrDefault(category.id, false),
                                children = buildList {
                                    feedsByCategoryId.getOrDefault(category.id, emptyList()).forEach { feed ->
                                        val feedUnreadCount = feedCounters.ifSuccess { feedCounters ->
                                            feedCounters.unreads.getOrDefault(feed.id, 0)
                                        }

                                        add(
                                            FlatDrawerItem(
                                                title = feed.title,
                                                icon = feedIconsById[feed.icon.iconId]?.let { icon ->
                                                    icon.ifSuccess {
                                                        FlatDrawerItem.Icon.Data(
                                                            bytes = it.data
                                                        )
                                                    }
                                                },
                                                badge = feedUnreadCount?.let { "$it" },
                                                selected = source == Source.Feed(feed.id)
                                            ) {
                                                coroutineScope.launch {
                                                    updateSourceSelection(Source.Feed(feed.id))
                                                }
                                            }
                                        )
                                    }
                                },
                                onToggle = {
                                    if (expandedCategories.getOrDefault(category.id, false)) {
                                        expandedCategories.remove(category.id)
                                    } else {
                                        expandedCategories[category.id] = true
                                    }
                                }
                            ) {
                                coroutineScope.launch {
                                    updateSourceSelection(Source.Category(category.id))
                                }
                            }
                        )
                    }
                }
            }

            val totalReadCount = feedCounters.ifSuccess { feedCounters ->
                feedCounters.reads.values.sum()
            }
            add(
                FlatDrawerItem(
                    title = "Read",
                    icon = FlatDrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Book
                    ),
                    badge = totalReadCount?.let { "$it" },
                    selected = source == Source.Read
                ) {
                    coroutineScope.launch {
                        updateSourceSelection(Source.Read)
                    }
                }
            )

            add(
                FlatDrawerItem(
                    title = "Search",
                    icon = FlatDrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Search
                    ),
                    selected = source is Source.Search
                ) {
                    coroutineScope.launch {
                        drawerState.close()
                        showSearchDialog = true
                    }
                }
            )

            add(
                FlatDrawerItem(
                    title = "Settings",
                    icon = FlatDrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Settings
                    ),
                ) {
                    // TODO: Implement settings
                }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val title = source.let { source ->
                            when (source) {
                                Source.Unread -> "Unread"
                                Source.Read -> "Read"
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
                            Text(it)
                        }
                    },
                    actions = {
                        areThereUnreadEntries.ifSuccess { areThereUnreadEntries ->
                            if (areThereUnreadEntries) {
                                IconButton(
                                    onClick = { showMarkAsReadConfirmationDialog = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.clickable(
                        onClick = {
                            if (source is Source.Search) {
                                showSearchDialog = true
                            }
                        },
                        indication = null,
                        interactionSource = null
                    )
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
                    entriesResponse.ifSuccess { entriesResponse ->
                        val currentPage = offset / PAGE_ENTRY_COUNT + 1
                        val totalPages = ceil(entriesResponse.total.toFloat() / PAGE_ENTRY_COUNT).toInt()

                        if (entriesResponse.entries.isNotEmpty()) {
                            LazyColumn (
                                state = scrollState,
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    entriesResponse.entries
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

                                            EntryListView(
                                                entries = entries.map { entry ->
                                                    buildFromAPIModel(entry)
                                                },
                                                onEntryTileClicked = onEntryOpenRequest
                                            )
                                        }
                                }

                                item {
                                    Row(Modifier.padding(12.dp)) {
                                        areThereUnreadEntries.ifSuccess { areThereUnreadEntries ->
                                            if (areThereUnreadEntries) {
                                                Button(
                                                    onClick = { showMarkAsReadConfirmationDialog = true }
                                                ) {
                                                    Text("Mark all as read")
                                                }
                                            }
                                        }

                                        Spacer(Modifier.weight(1f))

                                        if (offset > 0) {
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        updateOffset(max(offset - PAGE_ENTRY_COUNT, 0))
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

                                        TextButton(
                                            onClick = {
                                                showPageDialog = true
                                            }
                                        ) {
                                            Text("$currentPage / $totalPages")
                                        }

                                        if (offset < entriesResponse.total - PAGE_ENTRY_COUNT) {
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

                        if (showMarkAsReadConfirmationDialog) {
                            ConfirmationDialog(
                                titleText = "Mark page as read",
                                bodyText = "Are you sure you want to mark this page as read?",
                                confirmText = "Mark as read",
                                dismissText = "Cancel",
                                onConfirm = {
                                    coroutineScope.launch {
                                        viewModel.markAsRead(
                                            entryIds = entriesResponse.entries.map { it.id }
                                        )
                                        viewModel.reloadEntries()
                                        scrollState.scrollToItem(0)
                                    }
                                },
                                onRespond = { showMarkAsReadConfirmationDialog = false }
                            )
                        }

                        if (showPageDialog) {
                            PageDialog(
                                initialValue = currentPage,
                                totalPages = totalPages,
                                onSubmit = {
                                    coroutineScope.launch {
                                        updateOffset((it - 1) * PAGE_ENTRY_COUNT)
                                    }
                                },
                                onRespond = { showPageDialog = false }
                            )
                        }
                    }

                    if (showSearchDialog) {
                        SearchDialog(
                            initialTerm = source.let {
                                if (it is Source.Search) it.term else ""
                            },
                            onSubmit = { searchTerm ->
                                coroutineScope.launch {
                                    updateSourceSelection(Source.Search(term = searchTerm))
                                }
                            },
                            onRespond = { showSearchDialog = false }
                        )
                    }
                }
            }
        }
    }
}
