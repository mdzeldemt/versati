package com.liuvil.versati.activities.main.main.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.liuvil.versati.activities.main.main.home.category.add.AddCategoryDialog
import com.liuvil.versati.activities.main.main.home.category.edit.EditCategoryDialog
import com.liuvil.versati.activities.main.main.home.category.remove.RemoveCategoryDialog
import com.liuvil.versati.activities.main.main.home.entry.browse.BrowseEntriesView
import com.liuvil.versati.activities.main.main.home.entry.read.ReadEntryView
import com.liuvil.versati.activities.main.main.home.feed.add.AddFeedDialog
import com.liuvil.versati.activities.main.main.home.feed.edit.EditFeedDialog
import com.liuvil.versati.activities.main.main.home.feed.remove.RemoveFeedDialog
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
    data class RemoveCategory(
        val id: Int
    )

    @Serializable
    data object AddFeed

    @Serializable
    data class EditFeed(
        val id: Int
    )

    @Serializable
    data class RemoveFeed(
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
                onRemoveCategoryClicked = {
                    navController.navigate(
                        NavigationDestination.RemoveCategory(id = it)
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
                onRemoveFeedClicked = {
                    navController.navigate(
                        NavigationDestination.RemoveFeed(id = it)
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

        dialog<NavigationDestination.AddCategory> {
            AddCategoryDialog(
                onSubmit = {
                    navController.safePop()
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.EditCategory> {
            EditCategoryDialog(
                categoryId = it.toRoute<NavigationDestination.EditCategory>().id,
                onSubmit = {
                    navController.safePop()
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.RemoveCategory> {
            RemoveCategoryDialog(
                categoryId = it.toRoute<NavigationDestination.RemoveCategory>().id,
                onSubmit = {
                    navController.safePop()
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.AddFeed> {
            AddFeedDialog(
                onSubmit = {
                    navController.safePop()
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.EditFeed> {
            EditFeedDialog(
                feedId = it.toRoute<NavigationDestination.EditFeed>().id,
                onSubmit = {
                    navController.safePop()
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }

        dialog<NavigationDestination.RemoveFeed> {
            RemoveFeedDialog(
                categoryId = it.toRoute<NavigationDestination.RemoveFeed>().id,
                onSubmit = {
                    navController.safePop()
                },
                onDismiss = {
                    navController.safePop()
                }
            )
        }
    }
}