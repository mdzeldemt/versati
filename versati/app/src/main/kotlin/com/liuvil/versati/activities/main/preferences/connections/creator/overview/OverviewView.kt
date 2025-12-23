package com.liuvil.versati.activities.main.preferences.connections.creator.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.liuvil.versati.components.form.action.SimpleActionTile
import com.liuvil.versati.components.scaffold.action.BackButton
import com.liuvil.versati.framework.validation.Validation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OverviewView(
    name: String,
    baseURL: String,
    nameValidation: Validation,
    baseURLValidation: Validation,
    onNameChange: (String) -> Unit,
    onBaseURLChange: (String) -> Unit,
    onAuthenticationTileClicked: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Set up a connection")
                },
                navigationIcon = {
                    BackButton(
                        icon = Icons.Default.Close
                    ) {
                        onDismiss()
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSubmit()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { padding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                NameField(name, nameValidation) {
                    onNameChange(it)
                }
            }

            item {
                BaseURLField(baseURL, baseURLValidation) {
                    onBaseURLChange(it)
                }
            }

            item {
                HorizontalDivider()
            }

            item {
                Authentication {
                    onAuthenticationTileClicked()
                }
            }
        }
    }
}

@Composable
private fun NameField(
    value: String,
    validation: Validation,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text("Name")
        },
        isError = validation is Validation.Failure,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

@Composable
private fun BaseURLField(
    value: String,
    validation: Validation,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text("Base URL")
        },
        isError = validation is Validation.Failure,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

@Composable
private fun Authentication(
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Authentication",
        onClick = onClick
    )
}
