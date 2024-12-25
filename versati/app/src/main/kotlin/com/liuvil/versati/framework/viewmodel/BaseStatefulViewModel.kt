package com.liuvil.versati.framework.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class State {
    UNINITIALIZED,
    LOADING,
    IDLE
}

abstract class BaseStatefulViewModel<InitData>: BaseViewModel<InitData>() {

    private val _state = MutableStateFlow(State.UNINITIALIZED)

    val state: StateFlow<State> = _state

    suspend fun performLoading(block: suspend () -> Unit) {
        _state.value = State.LOADING
        try {
            block()
        } finally {
            _state.value = State.IDLE
        }
    }

}