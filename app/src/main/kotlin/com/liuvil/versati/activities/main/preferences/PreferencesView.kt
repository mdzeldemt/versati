package com.liuvil.versati.activities.main.preferences

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.liuvil.versati.activities.main.preferences.connection.ConnectionPreferencesView
import com.liuvil.versati.activities.main.preferences.miscellaneous.MiscellaneousPreferencesView
import com.liuvil.versati.activities.main.preferences.overview.OverviewPreferencesView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    object Home

    @Serializable
    object Connections

    @Serializable
    object Miscellaneous
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
            OverviewPreferencesView(
                onConnectionClicked = {
                    navController.navigate(NavigationDestination.Connections)
                },
                onMiscellaneousClicked = {
                    navController.navigate(NavigationDestination.Miscellaneous)
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

        composable<NavigationDestination.Miscellaneous> {
            MiscellaneousPreferencesView(
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}
