package com.liuvil.versati.activities.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.home.entry.EntryView
import com.liuvil.versati.activities.main.home.feed.FeedView
import com.liuvil.versati.activities.main.preferences.PreferencesView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    data object Feed

    @Serializable
    data class Entry(
        val id: Int
    )

    @Serializable
    data object Preferences
}

@Composable
fun HomeView(
    connectionID: Long
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Feed
    ) {
        composable<NavigationDestination.Feed> {
            FeedView(
                connectionID = connectionID,
                onEntryTileClicked = {
                    navController.navigate(NavigationDestination.Entry(id = it))
                },
                onPreferencesDrawerItemClicked = {
                    navController.navigate(NavigationDestination.Preferences)
                }
            )
        }

        composable<NavigationDestination.Entry> {
            EntryView(
                connectionID = connectionID,
                entryID = it.toRoute<NavigationDestination.Entry>().id,
                onDismiss = {
                    navController.safePop()
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