package com.liuvil.versati.repository

sealed class Origin {
    data object Local: Origin()
    data object Remote: Origin()
    data object LocalThenRemote: Origin()
}

class MissingLocalResourceException: RuntimeException()

class ResourceProvider<T>(
    private val local: suspend () -> T?,
    private val remote: suspend () -> T,
    private val sync: suspend (T) -> Unit
) {
    suspend fun provide(origin: Origin): T =
        when (origin) {
            Origin.Local ->
                local() ?: throw MissingLocalResourceException()

            Origin.Remote ->
                remote()
                    .also {
                        sync(it)
                    }

            Origin.LocalThenRemote ->
                local()
                    ?: remote().also {
                        sync(it)
                    }
        }
}