package com.liuvil.versati.activities.main.preferences.connections.creator

import androidx.compose.runtime.mutableStateOf
import com.liuvil.versati.framework.string.isValidURL
import com.liuvil.versati.framework.validation.validate
import com.liuvil.versati.framework.viewmodel.BaseViewModel
import com.liuvil.versati.preferences.CredentialRepository
import com.liuvil.versati.preferences.ConnectionRepository
import com.liuvil.versati.preferences.data.APIKeyCredentials
import com.liuvil.versati.preferences.data.BasicCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URL
import javax.inject.Inject

sealed class Mode {
    data object Creator: Mode()
    data class Editor(val connectionID: Long): Mode()
}

internal data class InitData(
    val mode: Mode
)

internal data class CreatorViewState(
    val name: String = "",
    val baseURL: String = "",
    val credentials: Credentials? = null,
) {
    sealed class Credentials {
        data class APIKey(
            val apiKey: String = ""
        ): Credentials()

        data class Basic(
            val username: String = "",
            val password: String = ""
        ): Credentials()
    }
}

@HiltViewModel
internal class CreatorViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val credentialRepository: CredentialRepository
): BaseViewModel<InitData>() {

    private lateinit var mode: Mode
    private lateinit var initialState: CreatorViewState

    var state = mutableStateOf(CreatorViewState())

    override suspend fun initialize(initData: InitData) {
        mode = initData.mode

        initialState = when (initData.mode) {
            is Mode.Creator ->
                CreatorViewState()

            is Mode.Editor -> {
                val connection = connectionRepository.getByID(initData.mode.connectionID)
                val credentials = credentialRepository.getByConnectionID(initData.mode.connectionID)

                CreatorViewState(
                    name = connection.name,
                    baseURL = connection.baseURL.toString(),
                    credentials = when (credentials) {
                        is APIKeyCredentials ->
                            CreatorViewState.Credentials.APIKey(
                                apiKey = credentials.apiKey
                            )

                        is BasicCredentials ->
                            CreatorViewState.Credentials.Basic(
                                username = credentials.username,
                                password = credentials.password
                            )
                    }
                )
            }
        }

        state.value = initialState
    }

    fun hasChanged() =
        state.value != initialState

    fun validateName() = validate {
        assert(
            state.value.name.isNotEmpty(),
            "The name of the connection cannot be empty."
        )
    }

    fun validateBaseURL() = validate {
        assert(
            state.value.baseURL.isNotEmpty(),
            "The base URL of the connection cannot be empty."
        )
        assert(
            isValidURL(state.value.baseURL),
            "The base URL of the connection must be a valid URL"
        )
    }

    fun validateCredentials() = validate {
        state.value.credentials.let {
            when (it) {
                is CreatorViewState.Credentials.APIKey ->
                    assert(
                        it.apiKey.isNotEmpty(),
                        "The authentication API key cannot be empty."
                    )

                is CreatorViewState.Credentials.Basic -> {
                    assert(
                        it.username.isNotEmpty(),
                        "The authentication username must not be empty."
                    )
                    assert(
                        it.password.isNotEmpty(),
                        "The authentication password must not be empty."
                    )
                }

                null -> fail("An authentication credential type must be selected.")
            }
        }
    }

    suspend fun submit() {
        val connection = mode.let {
            when (it) {
                is Mode.Creator ->
                    connectionRepository.create(
                        name = state.value.name,
                        baseURL = URL(state.value.baseURL)
                    )

                is Mode.Editor ->
                    connectionRepository.update(
                        id = it.connectionID,
                        name = state.value.name,
                        baseURL = URL(state.value.baseURL)
                    )
            }
        }

        state.value.credentials?.let {
            credentialRepository.upsert(
                connectionID = connection.id,
                credentials = when (it) {
                    is CreatorViewState.Credentials.APIKey ->
                        APIKeyCredentials(
                            apiKey = it.apiKey
                        )

                    is CreatorViewState.Credentials.Basic ->
                        BasicCredentials(
                            username = it.username,
                            password = it.password
                        )
                }
            )
        }
    }
}