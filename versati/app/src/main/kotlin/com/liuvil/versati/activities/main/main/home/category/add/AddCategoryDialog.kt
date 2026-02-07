package com.liuvil.versati.activities.main.main.home.category.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.liuvil.versati.framework.exception.detailedMessage
import com.liuvil.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch

private sealed class State {
    data object Loading: State()
    data object Input: State()
    data class Error(val exception: Exception): State()
}

@Composable
internal fun AddCategoryDialog(
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) = viewOf<AddCategoryDialogModel> { viewModel ->
    var title by viewModel.title

    var state by remember { mutableStateOf<State>(State.Input) }

    val coroutineScope = rememberCoroutineScope()

    state.let {
        when (it) {
            is State.Loading -> {}

            is State.Input -> {
                val isTitleError = title.isEmpty()

                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text("Add a category")
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                        ) {
                            TextField(
                                value = title,
                                label = {
                                    Text("Title")
                                },
                                isError = isTitleError,
                                onValueChange = { newValue ->
                                    title = newValue
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            enabled = !isTitleError,
                            onClick = {
                                coroutineScope.launch {
                                    state = State.Loading

                                    try {
                                        viewModel.createCategory()
                                    } catch (exception: Exception) {
                                        state = State.Error(exception)
                                        return@launch
                                    }

                                    onSubmit()
                                }
                            }
                        ) {
                            Text("Add category")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            is State.Error ->
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text("Error when creating category")
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
