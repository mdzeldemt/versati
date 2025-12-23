package com.liuvil.versati.activities.main.preferences.connections.creator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liuvil.versati.activities.main.preferences.connections.creator.authentication.AuthenticationView
import com.liuvil.versati.activities.main.preferences.connections.creator.overview.OverviewView
import com.liuvil.versati.components.ConfirmationDialog
import com.liuvil.versati.components.ErrorDialog
import com.liuvil.versati.framework.navigation.safePop
import com.liuvil.versati.framework.validation.either
import com.liuvil.versati.framework.viewmodel.bindViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable object Overview
    @Serializable object Authentication
}

@Composable
fun CreatorView(
    mode: Mode,
    onDismiss: () -> Unit
) {
    val viewModel = bindViewModel<InitData, CreatorViewModel>(InitData(mode))
    var state by viewModel.state

    var showDismissConfirmationDialog by remember { mutableStateOf(false) }
    var submitErrorMessage by remember { mutableStateOf<String?>(null) }

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Overview
    ) {
        composable<NavigationDestination.Overview> {
            OverviewView(
                name = state.name,
                baseURL = state.baseURL,
                nameValidation = viewModel.validateName(),
                baseURLValidation = viewModel.validateBaseURL(),
                onNameChange = {
                    state = state.copy(
                        name = it
                    )
                },
                onBaseURLChange = {
                    state = state.copy(
                        baseURL = it
                    )
                },
                onAuthenticationTileClicked = {
                    navController.navigate(NavigationDestination.Authentication)
                },
                onSubmit = {
                    either(
                        viewModel.validateName(),
                        viewModel.validateBaseURL(),
                        viewModel.validateCredentials()
                    ).apply {
                        ifSuccess {
                            coroutineScope.launch {
                                viewModel.submit()
                                onDismiss()
                            }
                        }

                        ifFailure {
                            submitErrorMessage = it
                        }
                    }
                },
                onDismiss = {
                    if (viewModel.hasChanged()) {
                        showDismissConfirmationDialog = true
                    } else {
                        onDismiss()
                    }
                }
            )
        }

        composable<NavigationDestination.Authentication> {
            AuthenticationView(
                credentials = state.credentials,
                onCredentialsChange = {
                    state = state.copy(
                        credentials = it
                    )
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }

    if (showDismissConfirmationDialog) {
        ConfirmationDialog(
            titleText = "Discard changes",
            bodyText = "You have unsaved changes. Are you sure you want to discard them?",
            confirmText = "Discard",
            dismissText = "Cancel",
            onConfirm = {
                onDismiss()
            },
            onRespond = {
                showDismissConfirmationDialog = false
            }
        )
    }

    submitErrorMessage?.let { message ->
        ErrorDialog(
            titleText = "Invalid connection details",
            bodyText = message,
            onConfirm = {
                submitErrorMessage = null
            }
        )
    }
}
