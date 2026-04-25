package com.mdzeldemt.versati.activities.main.preferences.miscellaneous

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mdzeldemt.versati.components.form.action.SimpleActionTile
import com.mdzeldemt.versati.components.form.radio.RadioInput
import com.mdzeldemt.versati.components.scaffold.action.BackButton
import com.mdzeldemt.versati.framework.viewmodel.status.Status
import com.mdzeldemt.versati.framework.viewmodel.viewOf
import com.mdzeldemt.versati.preferences.ColorScheme

private sealed class Dialog {
    data object ColorSchemeInput: Dialog()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscellaneousPreferencesView(
    onDismiss: () -> Unit
) = viewOf<MiscellaneousPreferencesViewModel> { viewModel ->
    val colorScheme by viewModel.colorScheme.collectAsState()

    val colorSchemeStatus by viewModel.colorSchemeStatus.collectAsState()

    var activeDialog by remember { mutableStateOf<Dialog?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onLoadPreferences()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Miscellaneous")
                },
                navigationIcon = {
                    BackButton {
                        onDismiss()
                    }
                }
            )
        }
    ) { padding ->
        colorSchemeStatus.let { colorSchemeStatus ->
            when (colorSchemeStatus) {
                is Status.Loading -> {}

                is Status.Success ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            ColorSchemeTile(colorScheme) {
                                activeDialog = Dialog.ColorSchemeInput
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
            is Dialog.ColorSchemeInput ->
                ColorSchemeInputDialog(
                    initialValue = colorScheme,
                    onSubmit = { value ->
                        viewModel.onUpdateColorScheme(value)
                    },
                    onResponse = {
                        activeDialog = null
                    }
                )
        }
    }
}

@Composable
private fun ColorSchemeTile(
    value: ColorScheme,
    onClick: () -> Unit
) {
    SimpleActionTile(
        title = "Color scheme",
        subtitle =
            when (value) {
                ColorScheme.SYSTEM -> "Follow system preferences"
                ColorScheme.DARK -> "Dark"
                ColorScheme.LIGHT -> "Light"
            },
        icon = Icons.Default.ColorLens,
        onClick = onClick
    )
}

@Composable
private fun ColorSchemeInputDialog(
    initialValue: ColorScheme,
    onSubmit: (ColorScheme) -> Unit,
    onResponse: () -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onResponse,
        title = {
            Text("Set color scheme")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                listOf(ColorScheme.SYSTEM, ColorScheme.DARK, ColorScheme.LIGHT)
                    .forEach {
                        RadioInput(
                            title =
                                when (it) {
                                    ColorScheme.SYSTEM -> "Follow system preferences"
                                    ColorScheme.DARK -> "Dark"
                                    ColorScheme.LIGHT -> "Light"
                                },
                            selected = (it == value),
                            onClick = {
                                value = it
                            }
                        )
                    }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onResponse()
                    onSubmit(value)
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