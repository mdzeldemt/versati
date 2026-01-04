package com.liuvil.versati.activities.main.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.main.home.entry.EntryView
import com.liuvil.versati.activities.main.main.home.feed.FeedView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    data object Feed

    @Serializable
    data class Entry(
        val id: Int
    )
}

@Composable
fun HomeView(
    onPreferencesDrawerItemClicked: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Feed
    ) {
        composable<NavigationDestination.Feed> {
            FeedView(
                onEntryTileClicked = {
                    navController.navigate(NavigationDestination.Entry(id = it))
                },
                onPreferencesDrawerItemClicked = onPreferencesDrawerItemClicked
            )
        }

        composable<NavigationDestination.Entry> {
            EntryView(
                entryID = it.toRoute<NavigationDestination.Entry>().id,
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}