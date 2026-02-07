package com.liuvil.versati.activities.main.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.main.home.category.add.AddCategoryDialog
import com.liuvil.versati.activities.main.main.home.entry.browse.BrowseEntriesView
import com.liuvil.versati.activities.main.main.home.entry.read.ReadEntryView
import com.liuvil.versati.activities.main.main.home.feed.add.AddFeedDialog
import com.liuvil.versati.framework.navigation.safePop
import kotlinx.serialization.Serializable

private object NavigationDestination {
    @Serializable
    data object BrowseEntries

    @Serializable
    data class ReadEntry(
        val id: Int
    )

    @Serializable
    data object AddCategory

    @Serializable
    data class EditCategory(
        val id: Int
    )

    @Serializable
    data object AddFeed

    @Serializable
    data class EditFeed(
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
                onAddCategoryClicked = {
                    navController.navigate(
                        NavigationDestination.AddCategory
                    )
                },
                onEditCategoryClicked = {
                    navController.navigate(
                        NavigationDestination.EditCategory(id = it)
                    )
                },
                onAddFeedClicked = {
                    navController.navigate(
                        NavigationDestination.AddFeed
                    )
                },
                onEditFeedClicked = {
                    navController.navigate(
                        NavigationDestination.EditFeed(id = it)
                    )
                },
                onPreferencesClicked = onPreferencesClicked
            )
        }

        composable<NavigationDestination.ReadEntry> {
            ReadEntryView(
                entryID = it.toRoute<NavigationDestination.ReadEntry>().id,
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.AddFeed> {
            AddFeedDialog(
                onSubmit = {
                    navController.navigate(
                        NavigationDestination.BrowseEntries
                    )
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.AddCategory> {
            AddCategoryDialog(
                onSubmit = {
                    navController.navigate(
                        NavigationDestination.BrowseEntries
                    )
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}