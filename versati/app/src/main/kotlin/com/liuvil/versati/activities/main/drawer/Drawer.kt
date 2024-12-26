package com.liuvil.versati.activities.main.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SourceTree(
    val categories: List<Category>
)

data class Category(
    val id: Int,
    val title: String,
    val feeds: List<Feed>
)

data class Feed(
    val id: Int,
    val title: String
)

sealed interface DrawerNode {
    data object AllUnread: DrawerNode
    data object AllRead: DrawerNode

    data class Category(
        val id: Int
    ): DrawerNode

    data class Feed(
        val id: Int
    ): DrawerNode

    data object Settings: DrawerNode
}

@Composable
fun Drawer(
    sourceTree: SourceTree,
    selectedNode: DrawerNode?,
    padding: Dp = 8.dp,
    indentation: Dp = 12.dp,
    drawerState: DrawerState,
    onNodeClicked: (DrawerNode) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape
            ) {
                LazyColumn (
                    modifier = Modifier.padding(padding)
                ) {
                    item {
                        NavigationDrawerItem(
                            label = { Text("All unread") },
                            selected = (selectedNode == DrawerNode.AllUnread),
                            onClick = {
                                onNodeClicked(DrawerNode.AllUnread)
                            }
                        )
                    }

                    sourceTree.categories.forEach { category ->
                        item {
                            NavigationDrawerItem(
                                label = { Text(category.title) },
                                selected = selectedNode == DrawerNode.Category(category.id),
                                onClick = {
                                    onNodeClicked(DrawerNode.Category(category.id))
                                }
                            )
                        }

                        category.feeds.forEach { feed ->
                            item {
                                NavigationDrawerItem(
                                    label = { Text(feed.title) },
                                    selected = selectedNode == DrawerNode.Feed(feed.id),
                                    onClick = {
                                        onNodeClicked(DrawerNode.Feed(feed.id))
                                    },
                                    modifier = Modifier.padding(start = indentation)
                                )
                            }
                        }
                    }

                    item {
                        NavigationDrawerItem(
                            label = { Text("All read") },
                            selected = selectedNode == DrawerNode.AllRead,
                            onClick = {
                                onNodeClicked(DrawerNode.AllRead)
                            }
                        )
                    }

                    item {
                        NavigationDrawerItem(
                            label = { Text("Settings") },
                            selected = selectedNode == DrawerNode.Settings,
                            onClick = {
                                onNodeClicked(DrawerNode.Settings)
                            }
                        )
                    }
                }
            }
        }
    ) {
        content()
    }
}
