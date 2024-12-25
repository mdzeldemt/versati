package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.entry_list.EntryListView
import com.liuvil.versati.framework.viewmodel.State
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onEntryOpenRequest: (Int) -> Unit
) {
    val viewModel = bindViewModel<MainViewModel>()
    val state by viewModel.state.collectAsState()
    val feedTree by viewModel.feedTree.collectAsState()
    val entries by viewModel.entries.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val updateSelection: suspend (Selection) -> Unit = remember {
        {
            viewModel.performLoading {
                drawerState.close()
                viewModel.select(it)
                viewModel.loadEntries()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.performLoading {
            viewModel.loadAll()
        }
    }

    Drawer(
        feedTree = feedTree,
        drawerState = drawerState,
        onCategoryNodeClicked = {
           coroutineScope.launch {
               updateSelection(Selection.Category(id = it))
           }
        },
        onFeedNodeClicked = {
            coroutineScope.launch {
                updateSelection(Selection.Feed(id = it))
            }
        }
    ) {
        when (status) {
            State.UNINITIALIZED, State.LOADING ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }

            State.IDLE -> {
                if (entries.isNotEmpty()) {
                    LazyColumn (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            EntryListView(
                                entries,
                                onEntryTileTapped = {
                                    onEntryOpenRequest(it)
                                }
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.performLoading {
                                            viewModel.markPageAsRead()
                                            viewModel.loadEntries()
                                        }
                                    }
                                },
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Mark this page as read")
                            }
                        }
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("No entries found.")
                    }
                }
            }
        }
    }
}