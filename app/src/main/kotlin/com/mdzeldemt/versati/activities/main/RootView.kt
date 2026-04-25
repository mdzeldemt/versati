package com.mdzeldemt.versati.activities.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mdzeldemt.versati.activities.main.main.MainView
import com.mdzeldemt.versati.activities.main.preferences.PreferencesView
import com.mdzeldemt.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    object Main

    @Serializable
    object Preferences
}

@Composable
fun RootView() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Main
    ) {
        composable<NavigationDestination.Main> {
            MainView(
                onPreferencesClicked = {
                    navController.navigate(
                        NavigationDestination.Preferences
                    )
                }
            )
        }

        composable<NavigationDestination.Preferences> {
            PreferencesView(
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}