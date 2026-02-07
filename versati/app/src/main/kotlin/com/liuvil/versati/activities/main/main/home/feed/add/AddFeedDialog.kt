package com.liuvil.versati.activities.main.main.home.feed.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.liuvil.versati.components.form.menu.DropdownMenuField
import com.liuvil.versati.components.form.menu.DropdownMenuItem
import com.liuvil.versati.framework.exception.detailedMessage
import com.liuvil.versati.framework.string.isValidURL
import com.liuvil.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch

private sealed class State {
    data object Loading: State()
    data object Input: State()
    data class Error(val exception: Exception): State()
}

@Composable
internal fun AddFeedDialog(
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) = viewOf<AddFeedDialogModel> { viewModel ->
    var feedUrl by viewModel.feedUrl
    var categoryId by viewModel.categoryId
    val categories by viewModel.categories

    var state by remember { mutableStateOf<State>(State.Input) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        state = State.Input
    }

    state.let {
        when (it) {
            is State.Loading -> {}

            is State.Input -> {
                val isFeedUrlError = !isValidURL(feedUrl)
                val isCategoryIdError = categoryId == null

                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text("Add a feed")
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                        ) {
                            TextField(
                                value = feedUrl,
                                label = {
                                    Text("Feed URL")
                                },
                                isError = isFeedUrlError,
                                onValueChange = { newValue ->
                                    feedUrl = newValue
                                }
                            )

                            DropdownMenuField(
                                title = "Category",
                                items = categories.map { category ->
                                    DropdownMenuItem(
                                        key = category.id,
                                        title = category.title
                                    )
                                },
                                selection = categoryId,
                                isError = isCategoryIdError,
                                onSelectionChanged = { newValue ->
                                    categoryId = newValue
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            enabled = !isFeedUrlError && !isCategoryIdError,
                            onClick = {
                                coroutineScope.launch {
                                    state = State.Loading

                                    try {
                                        viewModel.createFeed()
                                    } catch (exception: Exception) {
                                        state = State.Error(
                                            exception = exception
                                        )
                                        return@launch
                                    }

                                    onSubmit()
                                }
                            }
                        ) {
                            Text("Add feed")
                        }
                    },
                    dismissButton = {
                        if (state is State.Input) {
                            TextButton(
                                onClick = onDismiss
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                )
            }

            is State.Error ->
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text("Error when adding a feed")
                    },
                    text = {
                        Text("${it.exception.detailedMessage}")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                state = State.Input
                            }
                        ) {
                            Text("Back")
                        }
                    }
                )
        }
    }
}
