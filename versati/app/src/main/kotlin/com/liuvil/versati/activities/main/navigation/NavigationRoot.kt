package com.liuvil.versati.activities.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.FeedView
import com.liuvil.versati.activities.main.entry.EntryView

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Main
    ) {
        composable<NavigationDestination.Main> {
            FeedView(
                onEntryOpenRequest = {
                    navController.navigate(NavigationDestination.Entry(id = it))
                }
            )
        }

        composable<NavigationDestination.Entry> {
            EntryView(
                id = it.toRoute<NavigationDestination.Entry>().id,
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}