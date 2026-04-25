package com.liuvil.versati.activities.main.main.home.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Today
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.liuvil.versati.activities.main.main.home.browser.dialog.category.add.AddCategoryDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.category.edit.EditCategoryDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.category.remove.RemoveCategoryDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.entry.search.SearchEntriesDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.feed.add.AddFeedDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.feed.add.Category
import com.liuvil.versati.activities.main.main.home.browser.dialog.feed.edit.EditFeedDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.feed.remove.RemoveFeedDialog
import com.liuvil.versati.activities.main.main.home.browser.dialog.feed.status.FeedStatusDialog
import com.liuvil.versati.components.BlockingBox
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.components.ErrorDialog
import com.liuvil.versati.components.WrapperLayout
import com.liuvil.versati.components.drawer.DrawerErrorLabel
import com.liuvil.versati.components.drawer.DrawerItem
import com.liuvil.versati.components.drawer.DrawerItemBadge
import com.liuvil.versati.components.drawer.DrawerItemGroup
import com.liuvil.versati.components.drawer.DrawerItemIcon
import com.liuvil.versati.components.drawer.DrawerItemTitleLabel
import com.liuvil.versati.components.drawer.DrawerSectionHeader
import com.liuvil.versati.components.drawer.DrawerSectionHeaderButton
import com.liuvil.versati.components.drawer.DrawerSectionHeaderTitleLabel
import com.liuvil.versati.components.LargeActionButton
import com.liuvil.versati.components.SmallActionButton
import com.liuvil.versati.components.sheet.ActionBottomSheet
import com.liuvil.versati.components.sheet.ActionBottomSheetHeader
import com.liuvil.versati.components.sheet.ActionBottomSheetItem
import com.liuvil.versati.framework.compose.scrollToStart
import com.liuvil.versati.framework.compose.showSnackbar
import com.liuvil.versati.framework.date.formatHumanReadable
import com.liuvil.versati.framework.kotlin.runIf
import com.liuvil.versati.framework.throwable.detailedMessage
import com.liuvil.versati.framework.time.toHumanReadable
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.framework.viewmodel.status.fold
import com.liuvil.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch
import java.net.URL
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId

private val FAB_BAR_HEIGHT = 72.dp

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
    object AddCategory {
        data object Input: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    object EditCategory {
        data class Input(val id: Int): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    object RemoveCategory {
        data class Confirmation(val id: Int): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    data class FeedStatus(
        val id: Int
    ): Dialog()

    object RefreshFeed {
        data class Confirmation(val id: Int): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    object AddFeed {
        data object Input: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    object EditFeed {
        data class Input(val id: Int): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    object RemoveFeed {
        data class Confirmation(val id: Int): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }

    data class SearchEntries(
        val initialTerm: String
    ): Dialog()

    object MarkEntriesAsRead {
        data class Confirmation(val entryIds: List<Int>): Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserView(
    onEntryClicked: (Int) -> Unit,
    onPreferencesClicked: () -> Unit
) = viewOf<BrowserViewModel> { viewModel ->
    val source by viewModel.source.collectAsState()
    val offset by viewModel.offset.collectAsState()
    val categoriesById by viewModel.categoriesById.collectAsState()
    val feedsById by viewModel.feedsById.collectAsState()
    val iconsById by viewModel.iconsById.collectAsState()
    val entriesById by viewModel.entriesById.collectAsState()
    val totalEntries by viewModel.totalEntries.collectAsState()

    val categoriesStatus by viewModel.categoriesStatus.collectAsState()
    val feedsStatus by viewModel.feedsStatus.collectAsState()
    val entriesStatus by viewModel.entriesStatus.collectAsState()

    // Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val expandedCategories = remember { mutableStateMapOf<Int, Boolean>() }

    // Entry List
    val entryListState = rememberLazyListState()

    // Dialogs and Menus
    var activeModal by remember { mutableStateOf<Modal?>(null) }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (entriesById.isEmpty()) {
            viewModel.onReloadAllCategories()
            viewModel.onReloadAllFeedsAndIcons()
            viewModel.onReloadAllEntries()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.AddCategory.Success -> {
                    drawerState.close()
                    viewModel.onSelectSource(Source.Category(id = event.categoryId))
                    snackbarHostState.showSnackbar("Category successfully added")
                }

                is Event.AddCategory.Failure ->
                    snackbarHostState.showSnackbar("Failed to add category", "Details") {
                        activeModal = Dialog.AddCategory.Failure(event.reason)
                    }

                is Event.EditCategory.Success ->
                    viewModel.onSelectSource(Source.Category(id = event.categoryId))

                is Event.EditCategory.Failure ->
                    snackbarHostState.showSnackbar("Failed to edit category", "Details") {
                        activeModal = Dialog.EditCategory.Failure(event.reason)
                    }

                is Event.RemoveCategory.Success -> {
                    source.let { source ->
                        if (source == Source.Category(id = event.categoryId)
                            || source is Source.Feed && feedsById[source.id]?.categoryId == event.categoryId) {
                            viewModel.onSelectSource(Source.Unread)
                        }
                    }

                    snackbarHostState.showSnackbar("Category successfully removed")
                }

                is Event.RemoveCategory.Failure ->
                    snackbarHostState.showSnackbar("Failed to remove category", "Details") {
                        activeModal = Dialog.RemoveCategory.Failure(event.reason)
                    }

                is Event.RefreshFeed.Success -> {
                    drawerState.close()
                    viewModel.onSelectSource(Source.Feed(id = event.feedId))
                    snackbarHostState.showSnackbar("Feed successfully refreshed")
                }

                is Event.RefreshFeed.Failure ->
                    snackbarHostState.showSnackbar("Failed to refresh feed", "Details") {
                        activeModal = Dialog.RefreshFeed.Failure(event.reason)
                    }

                is Event.AddFeed.Success -> {
                    drawerState.close()
                    viewModel.onSelectSource(Source.Feed(id = event.feedId))
                    snackbarHostState.showSnackbar("Feed successfully added")
                }

                is Event.AddFeed.Failure ->
                    snackbarHostState.showSnackbar("Failed to add feed", "Details") {
                        activeModal = Dialog.AddFeed.Failure(event.reason)
                    }

                is Event.EditFeed.Success ->
                    viewModel.onSelectSource(Source.Feed(id = event.feedId))

                is Event.EditFeed.Failure ->
                    snackbarHostState.showSnackbar("Failed to edit feed", "Details") {
                        activeModal = Dialog.EditFeed.Failure(event.reason)
                    }

                is Event.RemoveFeed.Success -> {
                    if (source == Source.Feed(id = event.feedId)) {
                        viewModel.onSelectSource(Source.Unread)
                    }

                    snackbarHostState.showSnackbar("Feed successfully removed")
                }

                is Event.RemoveFeed.Failure ->
                    snackbarHostState.showSnackbar("Failed to remove feed", "Details") {
                        activeModal = Dialog.RemoveFeed.Failure(event.reason)
                    }

                is Event.LoadEntries.Success ->
                    entryListState.scrollToStart()

                is Event.LoadEntries.Failure ->
                    snackbarHostState.showSnackbar("Failed to load entries")

                is Event.MarkAllEntriesAsRead.Success ->
                    snackbarHostState.showSnackbar("All entries successfully mark as read")

                is Event.MarkAllEntriesAsRead.Failure ->
                    snackbarHostState.showSnackbar("Failed to mark all entries as read", "Details") {
                        activeModal = Dialog.MarkEntriesAsRead.Failure(event.reason)
                    }
            }
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
                                DrawerItemTitleLabel("Unread")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.Today)
                            },
                            selected = source == Source.Unread,
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }

                                viewModel.onSelectSource(Source.Unread)
                            }
                        )

                        DrawerItem(
                            label = {
                                DrawerItemTitleLabel("Starred")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.StarOutline)
                            },
                            selected = source == Source.Starred,
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }

                                viewModel.onSelectSource(Source.Starred)
                            }
                        )

                        DrawerSectionHeader(
                            leadingContent = {
                                DrawerSectionHeaderTitleLabel("Categories and feeds")

                                if (feedsStatus == Status.Success) {
                                    val totalFailingFeeds = feedsById.values
                                        .count { it.parsingErrorCount > 0 }
                                    if (totalFailingFeeds > 0) {
                                        DrawerErrorLabel(
                                            "($totalFailingFeeds)"
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                DrawerSectionHeaderButton(
                                    icon = Icons.Default.MoreHoriz,
                                    onClick = {
                                        activeModal = Menu.CategoriesAndFeeds
                                    }
                                )
                            }
                        )

                        when (fold(categoriesStatus, feedsStatus)) {
                            is Status.Loading ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator()
                                }

                            else -> {
                                val categories = categoriesById.values.sortedBy { it.title }
                                val feedsByCategoryId = feedsById.values
                                    .sortedBy { it.title }
                                    .groupBy { it.categoryId }

                                categories.forEach { category ->
                                    val expanded = expandedCategories.getOrDefault(category.id, false)
                                    DrawerItemGroup(
                                        label = {
                                            DrawerItemTitleLabel(category.title)
                                        },
                                        expanded = expanded,
                                        selected = source == Source.Category(category.id),
                                        badge = {
                                            val categoryFailingFeeds = feedsById.values
                                                .filter { it.categoryId == category.id }
                                                .count { it.parsingErrorCount > 0 }
                                            if (categoryFailingFeeds > 0) {
                                                DrawerErrorLabel(
                                                    "(${categoryFailingFeeds})"
                                                )
                                            }
                                        },
                                        onClick = {
                                            coroutineScope.launch {
                                                drawerState.close()
                                            }

                                            viewModel.onSelectSource(Source.Category(category.id))
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
                                                    DrawerItemTitleLabel(feed.title)
                                                },
                                                icon = {
                                                    iconsById[feed.iconId]?.let {
                                                        DrawerItemIcon(it)
                                                    }
                                                },
                                                selected = source == Source.Feed(feed.id),
                                                badge = {
                                                    if (feed.parsingErrorCount > 0) {
                                                        DrawerItemBadge(
                                                            imageVector = Icons.Default.ErrorOutline,
                                                            error = true
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    coroutineScope.launch {
                                                        drawerState.close()
                                                    }

                                                    viewModel.onSelectSource(Source.Feed(feed.id))
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
                                DrawerItemTitleLabel("History")
                            },
                            icon = {
                                DrawerItemIcon(Icons.Outlined.History)
                            },
                            selected = source == Source.History,
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                }

                                viewModel.onSelectSource(Source.History)
                            }
                        )

                        DrawerItem(
                            label = {
                                DrawerItemTitleLabel("Search")
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
                            Column {
                                val title = source.let { source ->
                                    when (source) {
                                        is Source.Unread -> "Unread"
                                        is Source.History -> "History"
                                        is Source.Starred -> "Starred"
                                        is Source.Category -> categoriesById[source.id]?.title
                                        is Source.Feed -> feedsById[source.id]?.title
                                        is Source.Search -> "Search '${source.term}'"
                                    }
                                }

                                title?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleLarge,
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

                                if ((entriesStatus is Status.Success || entriesStatus is Status.Loading)
                                        && entriesById.isNotEmpty()) {
                                    Text(
                                        text = "${offset + 1} - ${offset + entriesById.size} of $totalEntries",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
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
                    isRefreshing = entriesStatus == Status.Loading,
                    onRefresh = {
                        coroutineScope.launch {
                            viewModel.onReloadAllEntries()
                        }
                    },
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    BlockingBox(
                        isBlocking = entriesStatus == Status.Loading
                    ) {
                        entriesStatus.let { entriesStatus ->
                            when (entriesStatus) {
                                is Status.Success, is Status.Loading -> {
                                    val entries = entriesById.values.sortedByDescending { it.publishedAt }
                                    if (entries.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            LazyColumn(
                                                state = entryListState,
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                contentPadding = PaddingValues(bottom = FAB_BAR_HEIGHT),
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                entries
                                                    .groupBy {
                                                        it.publishedAt
                                                            .atZoneSameInstant(ZoneId.systemDefault())
                                                            .toLocalDate()
                                                    }
                                                    .forEach { (date, entries) ->
                                                        item {
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
                                                        }

                                                        entries.forEach { entry ->
                                                            item {
                                                                EntryTile(
                                                                    title = entry.title,
                                                                    feedTitle = feedsById[entry.feedId]?.title ?: "...",
                                                                    timeSincePublished = Duration.between(
                                                                        entry.publishedAt,
                                                                        OffsetDateTime.now()
                                                                    ),
                                                                    content = entry.text,
                                                                    imageUrl = entry.imageUrl,
                                                                    isRead = entry.isRead,
                                                                    onClick = {
                                                                        onEntryClicked(entry.id)
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .height(FAB_BAR_HEIGHT)
                                                    .align(Alignment.BottomCenter)
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp)
                                                ) {
                                                    if (entries.any { !it.isRead }) {
                                                        LargeActionButton(
                                                            text = {
                                                                Text("Mark all as read")
                                                            },
                                                            icon = {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = "Confirm"
                                                                )
                                                            },
                                                            onClick = {
                                                                activeModal = Dialog.MarkEntriesAsRead.Confirmation(
                                                                    entryIds = entries.map {
                                                                        it.id
                                                                    }
                                                                )
                                                            }
                                                        )
                                                    }

                                                    Spacer(Modifier.weight(1f))

                                                    if (offset > 0) {
                                                        SmallActionButton(
                                                            icon = {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                                                    contentDescription = null
                                                                )
                                                            },
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    viewModel.onGoToPreviousPage()
                                                                }
                                                            }
                                                        )
                                                    }

                                                    if (offset + entriesById.size < totalEntries) {
                                                        SmallActionButton(
                                                            icon = {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                                                    contentDescription = null
                                                                )
                                                            },
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    viewModel.onGoToNextPage()
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else if (entriesStatus == Status.Success) {
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

                                is Status.Failure ->
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
                                                text = entriesStatus.reason.detailedMessage,
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
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
                            viewModel.onReloadAllCategories()
                            viewModel.onReloadAllFeedsAndIcons()
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

            is Menu.Feed ->
                feedsById[modal.id]?.let { feed ->
                    ActionBottomSheet(
                        onDismiss = {
                            activeModal = null
                        }
                    ) {
                        ActionBottomSheetHeader(feed.title)

                        ActionBottomSheetItem(
                            title = "View feed status",
                            icon = Icons.Default.Info
                        ) {
                            activeModal = Dialog.FeedStatus(
                                id = modal.id
                            )
                        }

                        ActionBottomSheetItem(
                            title = "Refresh feed",
                            icon = Icons.Default.Refresh
                        ) {
                            activeModal = Dialog.RefreshFeed.Confirmation(
                                id = modal.id
                            )
                        }

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

            is Dialog.AddCategory.Input ->
                AddCategoryDialog(
                    onSubmit = {
                        viewModel.onAddCategory(
                            title = it.title
                        )
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.AddCategory.Failure ->
                ErrorDialog(
                    titleText = "Failed to add category",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.EditCategory.Input ->
                categoriesById[modal.id]?.let { category ->
                    EditCategoryDialog(
                        initialTitle = category.title,
                        onSubmit = {
                            viewModel.onEditCategory(
                                id = modal.id,
                                title = it.title
                            )
                        },
                        onRespond = {
                            activeModal = null
                        }
                    )
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
                categoriesById[modal.id]?.let { category ->
                    RemoveCategoryDialog(
                        title = category.title,
                        onConfirm = {
                            viewModel.onRemoveCategory(
                                id = modal.id
                            )
                        },
                        onRespond = {
                            activeModal = null
                        }
                    )
                }

            is Dialog.RemoveCategory.Failure ->
                ErrorDialog(
                    titleText = "Failed to remove category",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )
            
            is Dialog.FeedStatus ->
                feedsById[modal.id]?.let { feed ->
                    FeedStatusDialog(
                        checkedAt = feed.checkedAt,
                        nextCheckAt = feed.nextCheckAt,
                        parsingErrorCount = feed.parsingErrorCount,
                        parsingErrorMessage = feed.parsingErrorMessage
                            .takeIf { it.isNotBlank() },
                        onDismiss = {
                            activeModal = null
                        }
                    )
                }

            is Dialog.RefreshFeed.Confirmation ->
                feedsById[modal.id]?.let { feed ->
                    ConfirmationDialog(
                        titleText = "Refresh feed",
                        bodyText = "Are you sure you want to refresh the feed '${feed.title}'?",
                        confirmText = "Refresh",
                        dismissText = "Cancel",
                        onConfirm = {
                            viewModel.onRefreshFeed(modal.id)
                        },
                        onRespond = {
                            activeModal = null
                        }
                    )
                }

            is Dialog.RefreshFeed.Failure ->
                ErrorDialog(
                    titleText = "Failed to refresh feed",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.AddFeed.Input ->
                AddFeedDialog(
                    categories = categoriesById.values
                        .sortedBy { it.title }
                        .map {
                            Category(
                                id = it.id,
                                title = it.title
                            )
                        },
                    onSubmit = {
                        viewModel.onAddFeed(
                            feedUrl = it.feedUrl,
                            categoryId = it.categoryId
                        )
                    },
                    onRespond = {
                        activeModal = null
                    }
                )

            is Dialog.AddFeed.Failure ->
                ErrorDialog(
                    titleText = "Failed to add feed",
                    bodyText = modal.reason.detailedMessage,
                    onConfirm = {
                        activeModal = null
                    }
                )

            is Dialog.EditFeed.Input ->
                feedsById[modal.id]?.let { feed ->
                    EditFeedDialog(
                        initialTitle = feed.title,
                        initialFeedUrl = feed.feedUrl,
                        initialCategoryId = feed.categoryId,
                        categories = categoriesById.values
                            .sortedBy { it.title }
                            .map {
                                com.liuvil.versati.activities.main.main.home.browser.dialog.feed.edit.Category(
                                    id = it.id,
                                    title = it.title
                                )
                            },
                        onSubmit = {
                            viewModel.onEditFeed(
                                id = modal.id,
                                title = it.title,
                                feedUrl = it.feedUrl,
                                categoryId = it.categoryId
                            )
                        },
                        onRespond = {
                            activeModal = null
                        }
                    )
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
                feedsById[modal.id]?.let { feed ->
                    RemoveFeedDialog(
                        title = feed.title,
                        onConfirm = {
                            viewModel.onRemoveFeed(
                                id = modal.id
                            )
                        },
                        onRespond = {
                            activeModal = null
                        }
                    )
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
                SearchEntriesDialog(
                    initialTerm = modal.initialTerm,
                    onSubmit = { searchTerm ->
                        coroutineScope.launch {
                            drawerState.close()
                        }

                        viewModel.onSelectSource(Source.Search(searchTerm))
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
                        viewModel.onMarkAllEntriesAsRead()
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

@Composable
private fun EntryTile(
    title: String,
    feedTitle: String,
    timeSincePublished: Duration,
    content: String,
    imageUrl: URL? = null,
    isRead: Boolean,
    onClick: () -> Unit
) {
    val text = @Composable {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "$feedTitle / ${toHumanReadable(timeSincePublished)}"
        )

        content.trim().let {
            if (it.isNotBlank()) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = it,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    Box(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(10.dp)
            .runIf(isRead) {
                alpha(0.5f)
            }
    ) {
        imageUrl?.let {
            WrapperLayout(
                pivotSize = 100.dp,
                pivotContent = {
                    EntryImage(it)
                },
                wrapperContent = { text() }
            )
        } ?: Column(content = { text() })
    }
}

@Composable
private fun EntryImage(
    imageUrl: URL
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl.toString())
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1.2f)
            .padding(start = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = 0.25f)
            ),
        error = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.BrokenImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(0.5f),
                )
            }
        }
    )
}
