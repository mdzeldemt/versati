package com.liuvil.versati.activities.main.preferences

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liuvil.versati.activities.main.preferences.connections.ConnectionPreferencesView
import com.liuvil.versati.activities.main.preferences.home.HomePreferencesView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    object Home

    @Serializable
    object Connections
}

@Composable
fun PreferencesView(
    onDismiss: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Home
    ) {
        composable<NavigationDestination.Home> {
            HomePreferencesView(
                onConnectionsTileClicked = {
                    navController.navigate(NavigationDestination.Connections)
                },
                onDismiss = onDismiss
            )
        }

        composable<NavigationDestination.Connections> {
            ConnectionPreferencesView(
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}
