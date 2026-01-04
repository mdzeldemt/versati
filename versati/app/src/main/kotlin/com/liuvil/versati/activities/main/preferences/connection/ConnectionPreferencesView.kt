package com.liuvil.versati.activities.main.preferences.connection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.components.form.action.SimpleActionTile
import com.liuvil.versati.components.form.text.SimpleTextField
import com.liuvil.versati.components.scaffold.action.BackButton
import com.liuvil.versati.framework.app.restartApp
import com.liuvil.versati.framework.string.isValidURL
import com.liuvil.versati.framework.viewmodel.viewOf
import com.liuvil.versati.preferences.data.APIKeyCredentials
import com.liuvil.versati.preferences.data.BasicCredentials
import com.liuvil.versati.preferences.data.Credentials
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL

private sealed class Dialog {
    data class BaseURLInput(
        val initialValue: URL?
    ): Dialog()

    data class CredentialsInput(
        val initialValue: Credentials?
    ): Dialog()

    data object RestartConfirmation: Dialog()
}

private enum class CredentialType {
    API_KEY,
    BASIC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConnectionPreferencesView(
    onDismiss: () -> Unit
) = viewOf<ConnectionPreferencesViewModel> { viewModel ->
    val state by viewModel.state.collectAsState()

    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Connection")
                },
                navigationIcon = {
                    BackButton {
                        onDismiss()
                    }
                }
            )
        },
    ) { padding ->
        state.ifSuccess { state ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    BaseURLTile(state.baseURL) {
                        activeDialog = Dialog.BaseURLInput(
                            initialValue = state.baseURL
                        )
                    }
                }

                item {
                    AuthenticationTile(
                        when (state.credentials) {
                            is APIKeyCredentials -> CredentialType.API_KEY
                            is BasicCredentials -> CredentialType.BASIC
                            null -> null
                        }
                    ) {
                        activeDialog = Dialog.CredentialsInput(
                            initialValue = state.credentials
                        )
                    }
                }
            }
        }
    }

    activeDialog?.let {
        when (it) {
            is Dialog.BaseURLInput ->
                BaseURLInputDialog(
                    initialValue = it.initialValue,
                    onSubmit = { value ->
                        coroutineScope.launch {
                            viewModel.updateBaseURL(value)
                        }

                        activeDialog =
                            if (value != it.initialValue) {
                                Dialog.RestartConfirmation
                            } else {
                                null
                            }
                    },
                    onRespond = {
                        activeDialog = null
                    }
                )

            is Dialog.CredentialsInput ->
                CredentialsInputDialog(
                    initialValue = it.initialValue,
                    onSubmit = { value ->
                        coroutineScope.launch {
                            viewModel.updateCredentials(value)
                        }

                        activeDialog =
                            if (value != it.initialValue) {
                                Dialog.RestartConfirmation
                            } else {
                                null
                            }
                    },
                    onRespond = {
                        activeDialog = null
                    }
                )

            is Dialog.RestartConfirmation ->
                ConfirmationDialog(
                    titleText = "Restart to apply changes",
                    bodyText = "To apply the changes immediately, you must restart the app. Do you want to do it now?",
                    confirmText = "Restart now",
                    dismissText = "Continue without restarting",
                    onConfirm = {
                        context.restartApp()
                    },
                    onDismiss = {
                        activeDialog = null
                    }
                )
        }
    }
}

@Composable
private fun BaseURLTile(
    value: URL?,
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Base URL",
        subtitle = value?.toString() ?: "Not yet set",
        onClick = onClick
    )
}

@Composable
private fun AuthenticationTile(
    credentialType: CredentialType?,
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Credentials",
        subtitle =
            when (credentialType) {
                CredentialType.API_KEY -> "API key"
                CredentialType.BASIC -> "Basic"
                null -> "Not yet set"
            },
        onClick = onClick
    )
}

@Composable
private fun BaseURLInputDialog(
    initialValue: URL?,
    onSubmit: (URL?) -> Unit,
    onRespond: () -> Unit
) {
    var value by remember { mutableStateOf(initialValue?.toString() ?: "") }
    val isError by remember {
        derivedStateOf {
            value.isNotEmpty() && !isValidURL(value)
        }
    }

    AlertDialog(
        onDismissRequest = onRespond,
        title = {
            Text("Set base URL")
        },
        text = {
            SimpleTextField(
                value = value,
                labelText = "Base URL",
                isError = isError,
                onValueChange = {
                    value = it
                }
            )
        },
        confirmButton = {
            TextButton(
                enabled = !isError,
                onClick = {
                    onSubmit(value.toHttpUrl().toUrl())
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onRespond()
            }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CredentialsInputDialog(
    initialValue: Credentials?,
    onSubmit: (Credentials?) -> Unit,
    onRespond: () -> Unit
) {
    var credentialType by remember {
        mutableStateOf(
            when (initialValue) {
                is APIKeyCredentials -> CredentialType.API_KEY
                is BasicCredentials -> CredentialType.BASIC
                null -> null
            }
        )
    }

    var apiKey by remember {
        mutableStateOf(
            if (initialValue is APIKeyCredentials) {
                initialValue.apiKey
            } else ""
        )
    }

    var username by remember {
        mutableStateOf(
            if (initialValue is BasicCredentials) {
                initialValue.username
            } else ""
        )
    }

    var password by remember {
        mutableStateOf(
            if (initialValue is BasicCredentials) {
                initialValue.password
            } else ""
        )
    }

    val isApiKeyError by remember {
        derivedStateOf {
            apiKey.isEmpty()
        }
    }

    val isUsernameError by remember {
        derivedStateOf {
            username.isEmpty()
        }
    }

    val isPasswordError by remember {
        derivedStateOf {
            password.isEmpty()
        }
    }

    AlertDialog(
        onDismissRequest = onRespond,
        title = {
            Text("Set credentials")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    listOf(CredentialType.API_KEY, CredentialType.BASIC, null)
                        .forEach {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        credentialType = it
                                    }
                            ) {
                                RadioButton(
                                    selected = (it == credentialType),
                                    onClick = null
                                )
                                Text(
                                    when (it) {
                                        CredentialType.API_KEY -> "API key"
                                        CredentialType.BASIC -> "Basic"
                                        null -> "None"
                                    }
                                )
                            }
                        }
                }

                credentialType?.let {
                    when (it) {
                        CredentialType.API_KEY ->
                            SimpleTextField(
                                value = apiKey,
                                labelText = "API key",
                                isError = isApiKeyError,
                                onValueChange = {
                                    apiKey = it
                                }
                            )

                        CredentialType.BASIC -> {
                            SimpleTextField(
                                value = username,
                                labelText = "Username",
                                isError = isUsernameError,
                                onValueChange = {
                                    username = it
                                }
                            )

                            SimpleTextField(
                                value = password,
                                labelText = "Password",
                                isError = isPasswordError,
                                onValueChange = {
                                    password = it
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled =
                    when (credentialType) {
                        CredentialType.API_KEY -> !isApiKeyError
                        CredentialType.BASIC -> !isUsernameError && !isPasswordError
                        null -> true
                    },
                onClick = {
                    onSubmit(
                        when (credentialType) {
                            CredentialType.API_KEY ->
                                APIKeyCredentials(
                                    apiKey = apiKey
                                )

                            CredentialType.BASIC ->
                                BasicCredentials(
                                    username = username,
                                    password = password
                                )

                            null -> null
                        }
                    )
                    onRespond()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onRespond()
            }) {
                Text("Cancel")
            }
        }
    )
}
