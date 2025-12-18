package com.liuvil.versati.activities.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.entry.EntryView
import com.liuvil.versati.activities.main.feed.FeedView
import com.liuvil.versati.activities.main.home.navigation.HomeNavigationDestination

@Composable
fun HomeView(
    serverID: Int
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeNavigationDestination.Feed
    ) {
        composable<HomeNavigationDestination.Feed> {
            FeedView(
                serverID = serverID,
                onEntryOpenRequest = {
                    navController.navigate(HomeNavigationDestination.Entry(id = it))
                }
            )
        }

        composable<HomeNavigationDestination.Entry> {
            EntryView(
                serverID = serverID,
                entryID = it.toRoute<HomeNavigationDestination.Entry>().id,
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}