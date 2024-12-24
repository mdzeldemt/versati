package com.liuvil.versati.activities.main

import androidx.compose.foundation.layout.Column
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

@Composable
fun Drawer(
    feedTree: FeedTree,
    padding: Dp = 8.dp,
    feedStartPadding: Dp = 12.dp,
    drawerState: DrawerState,
    onCategoryNodeClicked: (Int) -> Unit,
    onFeedNodeClicked: (Int) -> Unit,
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
                    feedTree.categoryNodes.forEach { categoryNode ->
                        item {
                            NavigationDrawerItem(
                                label = { Text(categoryNode.title) },
                                selected = false,
                                onClick = { onCategoryNodeClicked(categoryNode.id) }
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier.padding(start = feedStartPadding)
                            ) {
                                categoryNode.feedNodes.forEach { feedNode ->
                                    NavigationDrawerItem(
                                        label = { Text(feedNode.title) },
                                        selected = false,
                                        onClick = { onFeedNodeClicked(feedNode.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}