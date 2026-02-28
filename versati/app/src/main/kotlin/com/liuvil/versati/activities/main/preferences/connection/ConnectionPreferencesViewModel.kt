package com.liuvil.versati.activities.main.preferences.connection

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.Credentials
import com.liuvil.versati.preferences.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.net.URL
import javax.inject.Inject

internal sealed class ViewState {
    data object Loading: ViewState()
    data class Ready(
        val baseURL: URL?,
        val credentials: Credentials?
    )
}

@HiltViewModel
internal class ConnectionPreferencesViewModel @Inject constructor(
    private val preferenceStore: PreferenceStore
): BaseViewModel<Unit>() {
    val state =
        combine(
            preferenceStore.baseURL,
            preferenceStore.credentials
        ) { baseURL, credentials ->
            ViewState.Ready(
                baseURL,
                credentials
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ViewState.Loading
        )

    suspend fun updateBaseURL(
        value: URL?
    ) {
        preferenceStore.setBaseURL(value)
    }

    suspend fun updateCredentials(
        value: Credentials?
    ) {
        preferenceStore.setCredentials(value)
    }
}