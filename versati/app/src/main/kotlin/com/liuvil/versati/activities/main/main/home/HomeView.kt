package com.liuvil.versati.activities.main.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.main.home.entry.browse.BrowseEntriesView
import com.liuvil.versati.activities.main.main.home.entry.read.ReadEntryView
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    data object BrowseEntries

    @Serializable
    data class ReadEntry(
        val id: Int
    )
}

@Composable
fun HomeView(
    onPreferencesClicked: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.BrowseEntries
    ) {
        composable<NavigationDestination.BrowseEntries> {
            BrowseEntriesView(
                onEntryClicked = {
                    navController.navigate(
                        NavigationDestination.ReadEntry(id = it)
                    )
                },
                onPreferencesClicked = onPreferencesClicked
            )
        }

        composable<NavigationDestination.ReadEntry> {
            ReadEntryView(
                entryId = it.toRoute<NavigationDestination.ReadEntry>().id,
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}