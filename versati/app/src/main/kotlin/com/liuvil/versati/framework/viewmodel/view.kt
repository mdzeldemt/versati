package com.liuvil.versati.framework.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
inline fun <reified ViewModel: BaseViewModel<Unit>> viewOf(
    content: @Composable (ViewModel) -> Unit
) {
    viewOf<Unit, ViewModel>(Unit, content)
}

@Composable
inline fun <T, reified ViewModel: BaseViewModel<T>> viewOf(
    data: T,
    content: @Composable (ViewModel) -> Unit
) {
    val viewModel = hiltViewModel<ViewModel>()
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        viewModel.initialize(data)
        initialized = true
    }

    if (initialized) {
        content(viewModel)
    }
}