package com.liuvil.versati.activities.main.main.home.category.remove

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.liuvil.versati.framework.throwable.detailedMessage
import com.liuvil.versati.framework.viewmodel.viewOf
import kotlinx.coroutines.launch

private sealed class State {
    data object Loading: State()
    data object Confirmation: State()
    data class Error(val exception: Exception): State()
}

@Composable
internal fun RemoveCategoryDialog(
    categoryId: Int,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) = viewOf<InitData, RemoveCategoryDialogModel>(
    InitData(categoryId)
) { viewModel ->
    val title = viewModel.title

    var state by remember { mutableStateOf<State>(State.Loading) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadCategoryTitle()
        state = State.Confirmation
    }

    state.let {
        when (it) {
            is State.Loading -> {}

            is State.Confirmation ->
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = {
                        Text("Remove a category")
                    },
                    text = {
                        Text(
                            text = buildAnnotatedString {
                                append("Are you sure you want to remove the category ")

                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(title)
                                }

                                append("? ")

                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("This action cannot be undone.")
                                }
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = {
                                coroutineScope.launch {
                                    state = State.Loading

                                    try {
                                        viewModel.deleteCategory()
                                    } catch (exception: Exception) {
                                        state = State.Error(exception)
                                        return@launch
                                    }

                                    onSubmit()
                                }
                            }
                        ) {
                            Text("Remove category")
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

            is State.Error ->
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text("Error when removing category")
                    },
                    text = {
                        Text("${it.exception.detailedMessage}")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                state = State.Confirmation
                            }
                        ) {
                            Text("Back")
                        }
                    }
                )
        }
    }
}
