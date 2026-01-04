package com.liuvil.versati.framework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BaseViewModel<InitData>: ViewModel() {
    open suspend fun initialize(initData: InitData) {
        // Override
    }
}