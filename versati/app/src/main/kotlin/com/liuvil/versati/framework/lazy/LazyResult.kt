package com.liuvil.versati.framework.lazy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

sealed class LazyResult<T> {
    inline fun <R> ifNone(block: () -> R): R? {
        if (this is None) {
            return block()
        }
        return null
    }

    inline fun <R> ifSuccess(block: (T) -> R): R? {
        if (this is Success) {
            return block(value)
        }
        return null
    }

    inline fun <R> ifFailure(block: (Throwable) -> R): R? {
        if (this is Failure) {
            return block(reason)
        }
        return null
    }

    fun <U> map(
        block: (T) -> U
    ): LazyResult<U> =
        when (this) {
            is None -> None()
            is Loading -> Loading()
            is Success ->
                try {
                    Success(block(value))
                } catch (exception: Exception) {
                    Failure(exception)
                }
            is Failure ->
                Failure(reason)
        }
}

class None<T>: LazyResult<T>()
class Loading<T>: LazyResult<T>()
data class Success<T>(val value: T): LazyResult<T>()
data class Failure<T>(val reason: Throwable): LazyResult<T>()

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
    block: suspend () -> V?
) {
    state[key] = Loading()
    try {
        block()?.let {
            state[key] = Success(it)
        } ?: state.remove(key)
    } catch (exception: Exception) {
        state[key] = Failure(exception)
    }
}

fun <T> lazyObserve(
    flow: Flow<T>
): Flow<LazyResult<T>> =
    flow
        .map {
            Success(it)
        }
        .catch {
            Failure<T>(it)
        }

private suspend fun <T> handle(
    block: suspend () -> T
): LazyResult<T> =
    try {
        Success(block())
    } catch (exception: Exception) {
        Failure(exception)
    }
