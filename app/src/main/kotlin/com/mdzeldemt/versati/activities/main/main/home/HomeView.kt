package com.mdzeldemt.versati.activities.main.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mdzeldemt.versati.activities.main.main.home.browser.BrowserView
import com.mdzeldemt.versati.activities.main.main.home.reader.ReaderView
import com.mdzeldemt.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    data object Browser

    @Serializable
    data class Reader(
        val entryId: Int
    )
}

@Composable
fun HomeView(
    onPreferencesClicked: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Browser
    ) {
        composable<NavigationDestination.Browser> {
            BrowserView(
                onEntryClicked = {
                    navController.navigate(
                        NavigationDestination.Reader(entryId = it)
                    )
                },
                onPreferencesClicked = onPreferencesClicked
            )
        }

        composable<NavigationDestination.Reader> {
            ReaderView(
                entryId = it.toRoute<NavigationDestination.Reader>().entryId,
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}