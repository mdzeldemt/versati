package com.liuvil.versati.activities.main.preferences.browsing

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.preferences.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BrowsingPreferencesViewModel @Inject constructor(
    private val preferenceStore: PreferenceStore
): BaseViewModel<Unit>() {

    private val _entriesPerPage = MutableStateFlow(0)

    private val _entriesPerPageStatus = MutableStateFlow<Status>(Status.Loading)

    val entriesPerPage = _entriesPerPage.asStateFlow()

    val entriesPerPageStatus = _entriesPerPageStatus.asStateFlow()

    fun onLoadPreferences() {
        viewModelScope.launch {
            loadPreferences()
        }
    }

    fun onUpdateEntriesPerPage(
        value: Int
    ) {
        viewModelScope.launch {
            updateEntriesPerPage(value)
        }
    }

    private suspend fun loadPreferences() {
        _entriesPerPageStatus.value = Status.Loading

        runCatching {
            preferenceStore.entriesPerPage.first()
        }.onSuccess { entriesPerPage ->
            _entriesPerPage.value = entriesPerPage
            _entriesPerPageStatus.value = Status.Success
        }
    }

    private suspend fun updateEntriesPerPage(
        value: Int
    ) {
        _entriesPerPageStatus.value = Status.Loading

        runCatching {
            preferenceStore.setEntriesPerPage(value)
        }.onSuccess {
            _entriesPerPage.value = value
            _entriesPerPageStatus.value = Status.Success
        }
    }
}