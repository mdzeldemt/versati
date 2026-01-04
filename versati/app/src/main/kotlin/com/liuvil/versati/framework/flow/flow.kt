package com.liuvil.versati.framework.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, U> Flow<T?>.flatMap(
    transform: (T) -> U
) =
    map { value ->
        value?.let {
            transform(it)
        }
    }