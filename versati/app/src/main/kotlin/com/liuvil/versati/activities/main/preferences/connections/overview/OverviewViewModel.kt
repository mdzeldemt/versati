package com.liuvil.versati.activities.main.preferences.connections.overview

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.framework.lazy.LazyResult
import com.liuvil.versati.framework.lazy.None
import com.liuvil.versati.framework.lazy.lazyLoad
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.ConnectionRepository
import com.liuvil.versati.preferences.data.Connection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository
): BaseViewModel<Unit>() {

    private var _connections = mutableStateOf<LazyResult<List<Connection>>>(None())

    val connections: State<LazyResult<List<Connection>>> = _connections

    suspend fun reloadConnections() {
        lazyLoad(_connections) {
            connectionRepository.getAll()
        }
    }

    suspend fun deleteConnection(
        connectionID: Long
    ) {
        connectionRepository.delete(connectionID)
    }
}