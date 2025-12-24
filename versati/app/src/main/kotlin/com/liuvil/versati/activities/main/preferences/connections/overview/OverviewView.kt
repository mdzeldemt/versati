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

private sealed class Menu {
    data class Connection(
        val connectionID: Long
    ): Menu()
}

private sealed class Dialog {
    data class Deletion(
        val connectionID: Long
    ): Dialog()
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

    var activeMenu by remember { mutableStateOf<Menu?>(null) }
    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

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
                                activeMenu = Menu.Connection(
                                    connectionID = it.id
                                )
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

    activeMenu?.let {
        when (it) {
            is Menu.Connection ->
                ModalMenu(
                    items = listOf(
                        ModalMenuItem("Edit") {
                            onEditClicked(
                                it.connectionID
                            )
                        },
                        ModalMenuItem("Delete") {
                            activeDialog = Dialog.Deletion(
                                connectionID = it.connectionID
                            )
                        },
                    ),
                    onDismiss = {
                        activeMenu = null
                    }
                )
        }
    }

    activeDialog?.let {
        when (it) {
            is Dialog.Deletion ->
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
                        activeDialog = null
                    }
                )
        }
    }
}