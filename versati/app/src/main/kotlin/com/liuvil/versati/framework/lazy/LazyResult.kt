package com.liuvil.versati.framework.lazy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap

sealed class LazyResult<T> {
    inline fun ifNone(block: () -> Unit) {
        if (this is None) {
            block()
        }
    }

    inline fun ifSuccess(block: (T) -> Unit) {
        if (this is Success) {
            block(value)
        }
    }
}

class None<T>: LazyResult<T>()
class Loading<T>: LazyResult<T>()
data class Success<T>(val value: T): LazyResult<T>()
data class Failure<T>(val exception: Exception): LazyResult<T>()

suspend fun <T> lazyLoad(
    state: MutableState<LazyResult<T>>,
    block: suspend () -> T
) {
    state.value = Loading()
    state.value = handle(block)
}

suspend fun <K, V> lazyLoad(
    state: SnapshotStateMap<K, LazyResult<V>>,
    key: K,
    block: suspend () -> V
) {
    state[key] = Loading()
    state[key] = handle(block)
}

private suspend fun <T> handle(block: suspend () -> T): LazyResult<T> =
    try {
        Success(block())
    } catch (exception: Exception) {
        Failure(exception)
    }
