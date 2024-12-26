package com.liuvil.versati.framework.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

enum class Status {
    UNINITIALIZED,
    LOADING,
    IDLE
}

@Composable
fun rememberViewStatusScope(): ViewStatusScope =
    remember { ViewStatusScope() }

class ViewStatusScope {
    private val _status = mutableStateOf(Status.UNINITIALIZED)

    val status: State<Status> = _status

    suspend fun launchLoading(block: suspend () -> Unit) {
        _status.value = Status.LOADING
        try {
            block()
        } finally {
            _status.value = Status.IDLE
        }
    }
}
