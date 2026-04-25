package com.mdzeldemt.versati.framework.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun SnackbarHostState.showSnackbar(
    message: String,
    actionLabel: String,
    action: (SnackbarResult) -> Unit
) {
    val result = showSnackbar(message, actionLabel)
    action(result)
}