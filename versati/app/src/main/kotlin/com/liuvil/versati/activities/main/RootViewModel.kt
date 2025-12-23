package com.liuvil.versati.activities.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.PreferenceStore
import com.liuvil.versati.preferences.ConnectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val preferenceStore: PreferenceStore,
    private val connectionRepository: ConnectionRepository
): BaseViewModel<Unit>() {

    private val _connectionID = mutableStateOf<LazyResult<Long?>>(None())

    val connectionID: State<LazyResult<Long?>> = _connectionID

    suspend fun reloadConnectionID() {
        lazyLoad(_connectionID) {
            preferenceStore.getActiveConnectionID()
                ?: connectionRepository.getAll()
                    .firstOrNull()
                    ?.id
        }
    }
}
