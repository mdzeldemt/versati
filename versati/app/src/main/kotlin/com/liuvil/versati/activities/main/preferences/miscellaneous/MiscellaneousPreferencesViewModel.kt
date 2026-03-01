package com.liuvil.versati.activities.main.preferences.miscellaneous

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.preferences.ColorScheme
import com.liuvil.versati.preferences.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal sealed class ViewState {
    data object Loading: ViewState()
    data class Ready(val colorScheme: ColorScheme): ViewState()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MiscellaneousPreferencesViewModel @Inject constructor(
    private val preferenceStore: PreferenceStore
): BaseViewModel<Unit>() {

    private val _colorScheme = MutableStateFlow(ColorScheme.SYSTEM)

    private val _colorSchemeStatus = MutableStateFlow<Status>(Status.Loading)

    val colorScheme = _colorScheme.asStateFlow()

    val colorSchemeStatus = _colorSchemeStatus.asStateFlow()

    val state = preferenceStore.colorScheme
        .mapLatest {
            ViewState.Ready(
                colorScheme = it
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ViewState.Loading
        )

    fun onLoadPreferences() {
        viewModelScope.launch {
            _colorSchemeStatus.value = Status.Loading

            runCatching {
                preferenceStore.colorScheme.first()
            }.onSuccess { colorScheme ->
                _colorScheme.value = colorScheme
                _colorSchemeStatus.value = Status.Success
            }
        }
    }

    fun onUpdateColorScheme(
        value: ColorScheme
    ) {
        viewModelScope.launch {
            _colorSchemeStatus.value = Status.Loading

            runCatching {
                preferenceStore.setColorScheme(value)
            }.onSuccess {
                _colorScheme.value = value
                _colorSchemeStatus.value = Status.Success
            }
        }
    }
}