package com.liuvil.versati.activities.main.preferences.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.liuvil.versati.components.ErrorDialog
import com.liuvil.versati.components.form.action.SimpleActionTile
import com.liuvil.versati.components.form.radio.RadioInput
import com.liuvil.versati.components.scaffold.action.BackButton
import com.liuvil.versati.framework.date.formatHumanReadableLong
import com.liuvil.versati.framework.string.isValidUrl
import com.liuvil.versati.framework.throwable.detailedMessage
import com.liuvil.versati.framework.viewmodel.status.Status
import com.liuvil.versati.framework.viewmodel.viewOf
import com.liuvil.versati.preferences.ApiKeyCredentials
import com.liuvil.versati.preferences.BasicCredentials
import com.liuvil.versati.preferences.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL
import java.time.OffsetDateTime

private sealed class Dialog {
    data object BaseUrlInput: Dialog()
    data object CredentialsInput: Dialog()

    object Details {
        data object Success: Dialog()
        data class Failure(val reason: Throwable): Dialog()
    }
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
    val preferences by viewModel.preferences.collectAsState()
    val details by viewModel.details.collectAsState()

    val preferencesStatus by viewModel.preferencesStatus.collectAsState()
    val detailsStatus by viewModel.detailsStatus.collectAsState()

    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onLoadPreferences()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            activeDialog =
                when (event) {
                    is Event.LoadDetails.Success ->
                        Dialog.Details.Success

                    is Event.LoadDetails.Failure ->
                        Dialog.Details.Failure(event.reason)
                }
        }
    }

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
                },
                actions = {
                    if (preferencesStatus == Status.Success
                        && preferences.baseUrl != null && preferences.credentials != null) {
                        IconButton(
                            enabled = detailsStatus != Status.Loading,
                            onClick = {
                                viewModel.onReloadDetails()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
    ) { padding ->
        preferencesStatus.let { preferencesStatus ->
            when (preferencesStatus) {
                is Status.Loading ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }

                is Status.Success ->
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            BaseUrlTile(preferences.baseUrl) {
                                activeDialog = Dialog.BaseUrlInput
                            }
                        }

                        item {
                            AuthenticationTile(
                                when (preferences.credentials) {
                                    is ApiKeyCredentials -> CredentialType.API_KEY
                                    is BasicCredentials -> CredentialType.BASIC
                                    null -> null
                                }
                            ) {
                                activeDialog = Dialog.CredentialsInput
                            }
                        }
                    }

                is Status.Failure -> {
                    // TODO: Add error message
                }
            }
        }
    }

    activeDialog?.let { dialog ->
        when (dialog) {
            is Dialog.BaseUrlInput ->
                BaseUrlInputDialog(
                    initialValue = preferences.baseUrl,
                    onSubmit = { value ->
                        viewModel.onUpdateBaseUrl(value)
                    },
                    onResponse = {
                        activeDialog = null
                    }
                )

            is Dialog.CredentialsInput ->
                CredentialsInputDialog(
                    initialValue = preferences.credentials,
                    onSubmit = { value ->
                        viewModel.onUpdateCredentials(value)
                    },
                    onResponse = {
                        activeDialog = null
                    }
                )

            is Dialog.Details.Success ->
                AlertDialog(
                    onDismissRequest = {
                        activeDialog = null
                    },
                    title = {
                        Text("Connection details")
                    },
                    text = {
                        Text(
                            text = getConnectionDetailsText(
                                version = details!!.version,
                                commit = details!!.commit,
                                buildDate = details!!.buildDate,
                                goVersion = details!!.goVersion,
                                compiler = details!!.compiler,
                                arch = details!!.arch,
                                os = details!!.os
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { activeDialog = null }) {
                            Text("Close")
                        }
                    }
                )

            is Dialog.Details.Failure ->
                ErrorDialog(
                    titleText = "Failed to obtain connection details",
                    bodyText = dialog.reason.detailedMessage,
                    onConfirm = {
                        activeDialog = null
                    }
                )
        }
    }
}

@Composable
private fun BaseUrlTile(
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
private fun BaseUrlInputDialog(
    initialValue: URL?,
    onSubmit: (URL?) -> Unit,
    onResponse: () -> Unit
) {
    var value by remember {
        mutableStateOf(initialValue?.toString() ?: "")
    }

    val isError by remember {
        derivedStateOf {
            value.isNotEmpty() && !isValidUrl(value)
        }
    }

    AlertDialog(
        onDismissRequest = onResponse,
        title = {
            Text("Set base URL")
        },
        text = {
            TextField(
                value = value,
                label = {
                    Text("Base URL")
                },
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
                    onResponse()

                    if (value.isNotEmpty()) {
                        onSubmit(value.toHttpUrl().toUrl())
                    } else {
                        onSubmit(null)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onResponse()
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
    onResponse: () -> Unit
) {
    var credentialType by remember {
        mutableStateOf(
            when (initialValue) {
                is ApiKeyCredentials -> CredentialType.API_KEY
                is BasicCredentials -> CredentialType.BASIC
                null -> null
            }
        )
    }

    var apiKey by remember {
        mutableStateOf(
            if (initialValue is ApiKeyCredentials) {
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
        onDismissRequest = onResponse,
        title = {
            Text("Set credentials")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    listOf(null, CredentialType.API_KEY, CredentialType.BASIC)
                        .forEach {
                            RadioInput(
                                title =
                                    when (it) {
                                        null -> "None"
                                        CredentialType.API_KEY -> "API key"
                                        CredentialType.BASIC -> "Basic"
                                    },
                                selected = (it == credentialType),
                                onClick = {
                                    credentialType = it
                                }
                            )
                        }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    credentialType?.let {
                        when (it) {
                            CredentialType.API_KEY ->
                                TextField(
                                    value = apiKey,
                                    label = {
                                        Text("API key")
                                    },
                                    isError = isApiKeyError,
                                    onValueChange = {
                                        apiKey = it
                                    }
                                )

                            CredentialType.BASIC -> {
                                TextField(
                                    value = username,
                                    label = {
                                        Text("Username")
                                    },
                                    isError = isUsernameError,
                                    onValueChange = {
                                        username = it
                                    }
                                )

                                TextField(
                                    value = password,
                                    label = {
                                        Text("Password")
                                    },
                                    isError = isPasswordError,
                                    visualTransformation = PasswordVisualTransformation(),
                                    onValueChange = {
                                        password = it
                                    }
                                )
                            }
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
                    onResponse()
                    onSubmit(
                        when (credentialType) {
                            CredentialType.API_KEY ->
                                ApiKeyCredentials(
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
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onResponse()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun getConnectionDetailsText(
    version: String,
    commit: String,
    buildDate: OffsetDateTime,
    goVersion: String,
    compiler: String,
    arch: String,
    os: String
): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("version: ")
    }
    append(version)

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\ncommit: ")
    }
    append(commit)

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nbuild date: ")
    }
    append(buildDate.formatHumanReadableLong())

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\nGo version: ")
    }
    append(goVersion)

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\ncompiler: ")
    }
    append(compiler)

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\narchitecture: ")
    }
    append(arch)

    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("\noperating system: ")
    }
    append(os)
}