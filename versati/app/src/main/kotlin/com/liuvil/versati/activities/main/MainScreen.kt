package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.liuvil.versati.activities.main.drawer.DrawerNode
import com.liuvil.versati.components.BlockingBox
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.framework.view.Status
import com.liuvil.versati.framework.view.rememberViewStatusScope
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch

sealed interface SourceSelection {
    data object AllEntries: SourceSelection
    data class Category(val id: Int): SourceSelection
    data class Feed(val id: Int): SourceSelection
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEntryOpenRequest: (Int) -> Unit
) {
    val statusScope = rememberViewStatusScope()
    val status by statusScope.status

    val viewModel = bindViewModel<MainViewModel>()
    var selectedSource by viewModel.selectedSource
    val sourceTree by viewModel.sourceTree
    val feedViewContent by viewModel.feedViewContent

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollState = rememberLazyListState()
    var showMarkAsReadConfirmationDialog by remember { mutableStateOf(false) }

    val isRefreshing by remember {
        derivedStateOf { arrayOf(Status.UNINITIALIZED, Status.LOADING).contains(status) }
    }

    val coroutineScope = rememberCoroutineScope()

    val updateSourceSelection: suspend (SourceSelection) -> Unit = remember {
        {
            drawerState.close()
            selectedSource = it
            statusScope.launchLoading {
                viewModel.reloadEntries()
            }
            scrollState.scrollToItem(0)
        }
    }

    LaunchedEffect(Unit) {
        if (status == Status.UNINITIALIZED) {
            statusScope.launchLoading {
                viewModel.reloadCategories()
                viewModel.reloadFeeds()
                viewModel.reloadEntries()
            }
        }
    }

    Drawer(
        sourceTree = sourceTree,
        drawerState = drawerState,
        selectedNode = selectedSource.let {
            when (it) {
                is SourceSelection.AllEntries -> DrawerNode.AllEntries
                is SourceSelection.Category -> DrawerNode.Category(it.id)
                is SourceSelection.Feed -> DrawerNode.Feed(it.id)
            }
        },
        onNodeClicked = {
            coroutineScope.launch {
                drawerState.close()

                when (it) {
                    is DrawerNode.AllEntries -> updateSourceSelection(SourceSelection.AllEntries)
                    is DrawerNode.Category -> updateSourceSelection(SourceSelection.Category(it.id))
                    is DrawerNode.Feed -> updateSourceSelection(SourceSelection.Feed(it.id))
                    else -> {
                        // TODO: Implement remaining callbacks
                    }
                }
            }
        },
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    statusScope.launchLoading {
                        viewModel.reloadEntries()
                    }
                }
            },
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            BlockingBox(
                isBlocking = isRefreshing
            ) {
                if (status == Status.IDLE) {
                    LazyColumn (
                        state = scrollState,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            FeedView(
                                content = feedViewContent,
                                onEntryTileClicked = onEntryOpenRequest
                            )
                        }

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
            }

            if (showMarkAsReadConfirmationDialog) {
                ConfirmationDialog(
                    title = "Mark page as read",
                    text = "Are you sure you want to mark this page as read?",
                    confirmText = "Mark as read",
                    dismissText = "Cancel",
                    onConfirm = {
                        coroutineScope.launch {
                            statusScope.launchLoading {
                                viewModel.markPageAsRead()
                                viewModel.reloadEntries()
                            }
                            scrollState.scrollToItem(0)
                        }
                    },
                    onRespond = { showMarkAsReadConfirmationDialog = false }
                )
            }
        }
    }
}
