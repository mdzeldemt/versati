package com.liuvil.versati.activities.main.preferences.connections.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.components.form.action.SimpleActionTile
import com.liuvil.versati.components.menu.modal.ModalMenu
import com.liuvil.versati.components.menu.modal.ModalMenuItem
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch

private sealed class Selection {
    data object None: Selection()
    data class Single(val connectionID: Long): Selection()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OverviewView(
    onCreateClicked: () -> Unit,
    onEditClicked: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val viewModel = bindViewModel<OverviewViewModel>()
    val connections by viewModel.connections

    var selection by remember { mutableStateOf<Selection>(Selection.None) }
    var showActionMenu by remember { mutableStateOf(false) }
    var showDeletionConfirmationDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.reloadConnections()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Connections")
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClicked
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        connections.ifSuccess { connections ->
            if (connections.isNotEmpty()) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(padding)
                ) {
                    items(connections) {
                        SimpleActionTile(
                            title = it.name,
                            subtitle = it.baseURL.toString(),
                            onClick = {
                                onEditClicked(it.id)
                            },
                            onLongClick = {
                                selection = Selection.Single(
                                    connectionID = it.id
                                )
                                showActionMenu = true
                            }
                        )
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Text("You have no connections.")
                }
            }
        }
    }

    selection.let {
        if (it is Selection.Single) {
            if (showActionMenu) {
                ModalMenu(
                    items = listOf(
                        ModalMenuItem("Edit") {
                            onEditClicked(
                                it.connectionID
                            )
                        },
                        ModalMenuItem("Delete") {
                            showDeletionConfirmationDialog = true
                        },
                    ),
                    onDismiss = {
                        showActionMenu = false
                    }
                )
            }

            if (showDeletionConfirmationDialog) {
                ConfirmationDialog(
                    titleText = "Delete connection",
                    bodyText = "Are you sure you want to delete the selected connection? This action cannot be undone.",
                    confirmText = "Delete",
                    dismissText = "Cancel",
                    onConfirm = {
                        coroutineScope.launch {
                            viewModel.deleteConnection(
                                connectionID = it.connectionID
                            )
                            viewModel.reloadConnections()
                        }
                    },
                    onRespond = {
                        showDeletionConfirmationDialog = false
                    }
                )
            }
        }
    }
}