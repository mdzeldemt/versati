package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Arrangement
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
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onEntryOpenRequest: (Int) -> Unit
) {
    val viewModel = bindViewModel<MainViewModel>()
    val status by viewModel.status.collectAsState()
    val feedTree by viewModel.feedTree.collectAsState()
    val entries by viewModel.entries.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val updateSelection: suspend (Selection) -> Unit = remember {
        {
            drawerState.close()
            viewModel.select(it)
            viewModel.loadEntries()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAll()
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
        LazyColumn (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            when (status) {
                Status.UNINITIALIZED, Status.LOADING ->
                    item {
                        CircularProgressIndicator()
                    }

                Status.IDLE -> {
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
                            onClick = {},
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Mark this page as read")
                        }
                    }
                }
            }
        }
    }
}