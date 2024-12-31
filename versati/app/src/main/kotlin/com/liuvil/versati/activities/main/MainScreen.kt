package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.drawer.Drawer
import com.liuvil.versati.activities.main.drawer.DrawerItem
import com.liuvil.versati.activities.main.entry_list.buildFromAPIModel
import com.liuvil.versati.components.BlockingBox
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.framework.lazy.Loading
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch
import java.time.ZoneId

sealed interface SourceSelection {
    data object Unread: SourceSelection
    data object Read: SourceSelection
    data object Starred: SourceSelection
    data class Category(val id: Int): SourceSelection
    data class Feed(val id: Int): SourceSelection
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEntryOpenRequest: (Int) -> Unit
) {
    val viewModel = bindViewModel<MainViewModel>()
    var selectedSource by viewModel.selectedSource
    val categories by viewModel.categories
    val feeds by viewModel.feeds
    val feedIconsById = viewModel.feedIconsById
    val feedCounters by viewModel.feedCounters
    val entries by viewModel.entries

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollState = rememberLazyListState()
    var showMarkAsReadConfirmationDialog by remember { mutableStateOf(false) }
    val isRefreshing by remember {
        derivedStateOf { entries is None || entries is Loading }
    }

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

    val coroutineScope = rememberCoroutineScope()

    val updateSourceSelection: suspend (SourceSelection) -> Unit = remember {
        {
            drawerState.close()
            selectedSource = it
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

        entries.ifNone {
            viewModel.reloadEntries()
        }
    }

    Drawer(
        items = buildList {
            val totalUnreadCount = feedCounters.ifSuccess { feedCounters ->
                feedCounters.unreads.values.sum()
            }

            add(
                DrawerItem(
                    title = "Unread",
                    icon = DrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Today
                    ),
                    badge = totalUnreadCount?.let { "$it" },
                    selected = selectedSource == SourceSelection.Unread
                ) {
                    coroutineScope.launch {
                        updateSourceSelection(SourceSelection.Unread)
                    }
                }
            )

            add(
                DrawerItem(
                    title = "Starred",
                    icon = DrawerItem.Icon.Vector(
                        vector = Icons.Outlined.StarOutline
                    ),
                    selected = selectedSource == SourceSelection.Starred
                ) {
                    coroutineScope.launch {
                        updateSourceSelection(SourceSelection.Starred)
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
                            DrawerItem(
                                title = category.title,
                                badge = categoryUnreadCount?.let { "$it" },
                                selected = selectedSource == SourceSelection.Category(category.id)
                            ) {
                                coroutineScope.launch {
                                    updateSourceSelection(SourceSelection.Category(category.id))
                                }
                            }
                        )

                        feedsByCategoryId.getOrDefault(category.id, emptyList()).forEach { feed ->
                            val feedUnreadCount = feedCounters.ifSuccess { feedCounters ->
                                feedCounters.unreads[feed.id]
                            }

                            add(
                                DrawerItem(
                                    title = feed.title,
                                    icon = feedIconsById[feed.icon.iconId]?.let { icon ->
                                        icon.ifSuccess {
                                            DrawerItem.Icon.Data(
                                                bytes = it.data
                                            )
                                        }
                                    },
                                    badge = feedUnreadCount?.let { "$it" },
                                    selected = selectedSource == SourceSelection.Feed(feed.id)
                                ) {
                                    coroutineScope.launch {
                                        updateSourceSelection(SourceSelection.Feed(feed.id))
                                    }
                                }
                            )
                        }
                    }
                }
            }

            val totalReadCount = feedCounters.ifSuccess { feedCounters ->
                feedCounters.reads.values.sum()
            }
            add(
                DrawerItem(
                    title = "Read",
                    icon = DrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Book
                    ),
                    badge = totalReadCount?.let { "$it" },
                    selected = selectedSource == SourceSelection.Read
                ) {
                    coroutineScope.launch {
                        updateSourceSelection(SourceSelection.Read)
                    }
                }
            )

            add(
                DrawerItem(
                    title = "Settings",
                    icon = DrawerItem.Icon.Vector(
                        vector = Icons.Outlined.Settings
                    ),
                ) {
                    // TODO: Implement settings
                }
            )
        },
        drawerState = drawerState
    ) {
        Column {
            selectedSource.let { selectedSource ->
                Header(
                    title = when (selectedSource) {
                        SourceSelection.Unread -> "Unread"
                        SourceSelection.Read -> "Read"
                        SourceSelection.Starred -> "Starred"
                        is SourceSelection.Category ->
                            categoriesById.ifSuccess { categoriesById ->
                                categoriesById[selectedSource.id]?.title
                            }
                        is SourceSelection.Feed ->
                            feedsById.ifSuccess { feedsById ->
                                feedsById[selectedSource.id]?.title
                            }
                    },
                    buttons = buildList {
                        entries.ifSuccess {
                            add(
                                HeaderButton(
                                    icon = Icons.Filled.Check,
                                    onClick = { showMarkAsReadConfirmationDialog = true }
                                )
                            )
                        }
                    }
                )
            }


            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        viewModel.reloadEntries()
                    }
                },
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                BlockingBox(
                    isBlocking = isRefreshing
                ) {
                    entries.ifSuccess { entries ->
                        LazyColumn (
                            state = scrollState,
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                FeedView(
                                    content = FeedViewContent(
                                        entryGroups = entries
                                            .groupBy {
                                                it.publishedAt
                                                    .atZoneSameInstant(ZoneId.systemDefault())
                                                    .toLocalDate()
                                            }
                                            .map {
                                                EntryGroup.Timed(
                                                    date = it.key,
                                                    entries = it.value.map { entry ->
                                                        buildFromAPIModel(entry)
                                                    }
                                                )
                                            }
                                    ),
                                    onEntryTileClicked = onEntryOpenRequest
                                )
                            }

                            if (selectedSource != SourceSelection.Read) {
                                item {
                                    Button(
                                        onClick = { showMarkAsReadConfirmationDialog = true },
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text("Mark this page as read")
                                    }
                                }
                            }
                        }

                        if (showMarkAsReadConfirmationDialog) {
                            ConfirmationDialog(
                                title = "Mark page as read",
                                text = "Are you sure you want to mark this page as read?",
                                confirmText = "Mark as read",
                                dismissText = "Cancel",
                                onConfirm = {
                                    coroutineScope.launch {
                                        viewModel.markAsRead(
                                            entryIds = entries.map { it.id }
                                        )
                                        viewModel.reloadEntries()
                                        scrollState.scrollToItem(0)
                                    }
                                },
                                onRespond = { showMarkAsReadConfirmationDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
