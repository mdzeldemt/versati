package com.liuvil.versati.activities.main.preferences.connections.creator.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.liuvil.versati.activities.main.preferences.connections.creator.CreatorViewState
import com.liuvil.versati.components.form.selection.Item
import com.liuvil.versati.components.form.selection.SimpleSelectionField
import com.liuvil.versati.components.form.text.SimpleTextField
import com.liuvil.versati.components.scaffold.action.BackButton

private enum class CredentialType {
    API_KEY,
    BASIC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthenticationView(
    credentials: CreatorViewState.Credentials?,
    onCredentialsChange: (CreatorViewState.Credentials?) -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Set up authentication")
                },
                navigationIcon = {
                    BackButton {
                        onDismiss()
                    }
                }
            )
        },
    ) { padding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                CredentialTypeField(
                    value = credentials?.let {
                        when (it) {
                            is CreatorViewState.Credentials.APIKey -> CredentialType.API_KEY
                            is CreatorViewState.Credentials.Basic -> CredentialType.BASIC
                        }
                    },
                    onValueChange = { credentialType ->
                        onCredentialsChange(
                            credentialType?.let {
                                when (it) {
                                    CredentialType.API_KEY -> CreatorViewState.Credentials.APIKey()
                                    CredentialType.BASIC -> CreatorViewState.Credentials.Basic()
                                }
                            }
                        )
                    }
                )
            }

            item {
                HorizontalDivider()
            }

            credentials?.let { credentials ->
                when (credentials) {
                    is CreatorViewState.Credentials.APIKey ->
                        item {
                            APIKeyField(credentials.apiKey, false) {
                                onCredentialsChange(
                                    credentials.copy(
                                        apiKey = it
                                    )
                                )
                            }
                        }

                    is CreatorViewState.Credentials.Basic -> {
                        item {
                            UsernameField(credentials.username, false) {
                                onCredentialsChange(
                                    credentials.copy(
                                        username = it
                                    )
                                )
                            }
                        }

                        item {
                            PasswordField(credentials.password, false) {
                                onCredentialsChange(
                                    credentials.copy(
                                        password = it
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CredentialTypeField(
    value: CredentialType?,
    onValueChange: (CredentialType?) -> Unit
) {
    SimpleSelectionField(
        items = listOf(
            Item(
                key = null,
                label = "None selected"
            ),
            Item(
                key = CredentialType.API_KEY,
                label = "API key"
            ),
            Item(
                key = CredentialType.BASIC,
                label = "Basic"
            )
        ),
        selectedKey = value,
        onSelectedKeyChange = onValueChange
    ) {
        Text("Credential Type")
    }
}

@Composable
private fun APIKeyField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    SimpleTextField(
        value = value,
        labelText = "API key",
        isError = isError,
        onValueChange = {
            onValueChange(it)
        }
    )
}

@Composable
private fun UsernameField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    SimpleTextField(
        value = value,
        labelText = "Username",
        isError = isError,
        onValueChange = {
            onValueChange(it)
        }
    )
}

@Composable
private fun PasswordField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    SimpleTextField(
        value = value,
        labelText = "Password",
        isError = isError,
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = {
            onValueChange(it)
        }
    )
}
