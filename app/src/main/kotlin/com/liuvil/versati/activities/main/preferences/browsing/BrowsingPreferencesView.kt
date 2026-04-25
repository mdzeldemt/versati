package com.liuvil.versati.activities.main.preferences.browsing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.liuvil.versati.components.form.action.SimpleActionTile
import com.liuvil.versati.components.scaffold.action.BackButton
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.framework.viewmodel.viewOf

private const val MIN_ENTRIES_PER_PAGE = 10
private const val MAX_ENTRIES_PER_PAGE = 50

private sealed class Dialog {
    data object EntriesPerPageInput: Dialog()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowsingPreferencesView(
    onDismiss: () -> Unit
) = viewOf<BrowsingPreferencesViewModel> { viewModel ->
    val entriesPerPage by viewModel.entriesPerPage.collectAsState()

    val entriesPerPageStatus by viewModel.entriesPerPageStatus.collectAsState()

    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onLoadPreferences()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Browsing")
                },
                navigationIcon = {
                    BackButton {
                        onDismiss()
                    }
                }
            )
        }
    ) { padding ->
        entriesPerPageStatus.let { entriesPerPageStatus ->
            when (entriesPerPageStatus) {
                is Status.Loading -> {}

                is Status.Success ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            EntriesPerPageTile(entriesPerPage) {
                                activeDialog = Dialog.EntriesPerPageInput
                            }
                        }
                    }

                is Status.Failure -> {
                    // TODO: Add error message
                }
            }
        }
    }

    activeDialog?.let { dialog ->
        when (dialog) {
            is Dialog.EntriesPerPageInput ->
                EntriesPerPageInputDialog(
                    initialValue = entriesPerPage,
                    onSubmit = { value ->
                        viewModel.onUpdateEntriesPerPage(value)
                    },
                    onResponse = {
                        activeDialog = null
                    }
                )
        }
    }
}

@Composable
private fun EntriesPerPageTile(
    value: Int,
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Entries per page",
        subtitle = "$value",
        icon = Icons.Default.Numbers,
        onClick = onClick
    )
}

@Composable
private fun EntriesPerPageInputDialog(
    initialValue: Int,
    onSubmit: (Int) -> Unit,
    onResponse: () -> Unit
) {
    var value by remember {
        "$initialValue".let {
            mutableStateOf(TextFieldValue(it, TextRange(it.length)))
        }
    }

    val isError by remember {
        derivedStateOf {
            value.text.isEmpty()
                || !value.text.isDigitsOnly()
                || value.text.toInt() < MIN_ENTRIES_PER_PAGE
                || value.text.toInt() > MAX_ENTRIES_PER_PAGE
        }
    }

    AlertDialog(
        onDismissRequest = onResponse,
        title = {
            Text("Set entries per page")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("This number must be between $MIN_ENTRIES_PER_PAGE and $MAX_ENTRIES_PER_PAGE.")

                TextField(
                    value = value,
                    label = {
                        Text("Entries per page")
                    },
                    isError = isError,
                    onValueChange = {
                        if (it.text.isNotEmpty() || it.text.isDigitsOnly()) {
                            value = it
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isError,
                onClick = {
                    onResponse()
                    onSubmit(value.text.toInt())
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onResponse()
            }) {
                Text("Cancel")
            }
        }
    )
}