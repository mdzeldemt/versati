package com.liuvil.versati.activities.main.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ExpandableDrawerItem(
    override val title: String,
    override val badge: String? = null,
    override val selected: Boolean = false,
    val expanded: Boolean,
    val children: List<DrawerItem>,
    val onToggle: () -> Unit,
    override val onClick: () -> Unit,
): DrawerItem(title, badge, selected, onClick)

@Composable
fun Drawer(
    items: List<DrawerItem>,
    padding: Dp = 8.dp,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape
            ) {
                Column (
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    items.forEach {
                        DrawerItem(it)
                    }
                }
            }
        }
    ) {
        content()
    }
}
