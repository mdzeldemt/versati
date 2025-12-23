package com.liuvil.versati.activities.main.preferences.connections

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.preferences.connections.creator.CreatorView
import com.liuvil.versati.activities.main.preferences.connections.creator.Mode
import com.liuvil.versati.activities.main.preferences.connections.overview.OverviewView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    object Overview

    @Serializable
    object Creator

    @Serializable
    data class Editor(val connectionID: Long)
}

@Composable
fun ConnectionPreferencesView(
    onDismiss: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Overview
    ) {
        composable<NavigationDestination.Overview> {
            OverviewView(
                onCreateClicked = {
                    navController.navigate(
                        NavigationDestination.Creator
                    )
                },
                onEditClicked = { connectionID ->
                    navController.navigate(
                        NavigationDestination.Editor(connectionID)
                    )
                },
                onDismiss = {
                    onDismiss()
                }
            )
        }

        composable<NavigationDestination.Creator> {
            CreatorView(
                mode = Mode.Creator,
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        composable<NavigationDestination.Editor> {
            CreatorView(
                mode = Mode.Editor(
                    connectionID = it.toRoute<NavigationDestination.Editor>().connectionID
                ),
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}