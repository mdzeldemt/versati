package com.liuvil.versati.framework.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Composable
inline fun <reified ViewModel: BaseViewModel<Unit>> bindViewModel(): ViewModel {
    return bindViewModel<Unit, ViewModel>(Unit)
}

@Composable
inline fun <InitData, reified ViewModel: BaseViewModel<InitData>> bindViewModel(
    initData: InitData
): ViewModel {
    val viewModel = hiltViewModel<ViewModel>()
    DisposableEffect(Unit) {
        viewModel.init(initData)
        onDispose {}
    }
    return viewModel
}

abstract class BaseViewModel<InitData>: ViewModel() {

    protected open suspend fun initialize(initData: InitData) {
        // Override
    }

    fun init(initData: InitData) {
        viewModelScope.launch {
            initialize(initData)
        }
    }

}