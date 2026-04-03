package com.liuvil.versati.activities.main.preferences.connection

import androidx.lifecycle.viewModelScope
import com.liuvil.versati.activities.main.preferences.connection.use_case.GetDetailsUseCase
import com.liuvil.versati.activities.main.preferences.connection.use_case.GetPreferencesUseCase
import com.liuvil.versati.activities.main.preferences.connection.use_case.UpdateBaseUrlUseCase
import com.liuvil.versati.activities.main.preferences.connection.use_case.UpdateCredentialsUseCase
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.preferences.Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URL
import java.time.OffsetDateTime
import javax.inject.Inject

internal data class Preferences(
    val baseUrl: URL?,
    val credentials: Credentials?
)

internal data class Details(
    val version: String,
    val commit: String,
    val buildDate: OffsetDateTime,
    val goVersion: String,
    val compiler: String,
    val arch: String,
    val os: String
)

internal sealed class Event {
    object LoadDetails {
        data object Success: Event()
        data class Failure(val reason: Throwable): Event()
    }
}

@HiltViewModel
internal class ConnectionPreferencesViewModel @Inject constructor(
    private val getPreferences: GetPreferencesUseCase,
    private val updateBaseUrl: UpdateBaseUrlUseCase,
    private val updateCredentials: UpdateCredentialsUseCase,
    private val getDetails: GetDetailsUseCase,
): BaseViewModel<Unit>() {

    private val _preferences = MutableStateFlow(Preferences(baseUrl = null, credentials = null))
    private val _details = MutableStateFlow<Details?>(null)

    private val _preferencesStatus = MutableStateFlow<Status>(Status.Loading)
    private val _detailsStatus = MutableStateFlow<Status>(Status.Success)

    private val _events = MutableSharedFlow<Event>()

    val preferences = _preferences.asStateFlow()
    val details = _details.asStateFlow()

    val preferencesStatus = _preferencesStatus.asStateFlow()
    val detailsStatus = _detailsStatus.asStateFlow()

    val events = _events.asSharedFlow()

    fun onLoadPreferences() {
        viewModelScope.launch {
            _preferencesStatus.value = Status.Loading

            getPreferences()
                .onSuccess { preferences ->
                    _preferences.value = preferences
                    _preferencesStatus.value = Status.Success
                }.onFailure { reason ->
                    _preferencesStatus.value = Status.Failure(reason)
                }
        }
    }

    fun onReloadDetails() {
        viewModelScope.launch {
            _detailsStatus.value = Status.Loading

            getDetails()
                .onSuccess { details ->
                    _details.value = details
                    _detailsStatus.value = Status.Success

                    _events.emit(Event.LoadDetails.Success)
                }
                .onFailure { reason ->
                    _detailsStatus.value = Status.Failure(reason)

                    _events.emit(Event.LoadDetails.Failure(reason))
                }
        }
    }

    fun onUpdateBaseUrl(
        value: URL?
    ) {
        viewModelScope.launch {
            _preferencesStatus.value = Status.Loading

            updateBaseUrl(value)
                .onSuccess {
                    _preferences.update {
                        it.copy(
                            baseUrl = value
                        )
                    }

                    _preferencesStatus.value = Status.Success
                }
                .onFailure { reason ->
                    _preferencesStatus.value = Status.Failure(reason)
                }
        }
    }

    fun onUpdateCredentials(
        value: Credentials?
    ) {
        viewModelScope.launch {
            _preferencesStatus.value = Status.Loading

            updateCredentials(value)
                .onSuccess {
                    _preferences.update {
                        it.copy(
                            credentials = value
                        )
                    }

                    _preferencesStatus.value = Status.Success
                }
                .onFailure { reason ->
                    _preferencesStatus.value = Status.Failure(reason)
                }
        }
    }
}