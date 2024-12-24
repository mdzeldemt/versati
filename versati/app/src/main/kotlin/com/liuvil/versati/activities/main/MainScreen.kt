package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.entry_list.EntryListView
import com.liuvil.versati.framework.viewmodel.bindViewModel

@Composable
fun MainScreen(
    onEntryOpenRequest: (Int) -> Unit
) {
    val viewModel = bindViewModel<MainViewModel>()
    val status by viewModel.status.collectAsState()
    val entries by viewModel.entries.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEntries()
    }

    LazyColumn (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        when (status) {
            Status.UNINITIALIZED -> {}

            Status.LOADING ->
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