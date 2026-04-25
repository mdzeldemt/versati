package com.mdzeldemt.versati.framework.viewmodel.status

sealed class Status {
    data object Loading: Status()
    data object Success: Status()
    data class Failure(val reason: Throwable): Status()
}

fun fold(
    vararg statuses: Status
): Status =
    if (statuses.any { it == Status.Loading }) {
        Status.Loading
    } else {
        statuses
            .firstOrNull { it is Status.Failure }
            ?: Status.Success
    }
