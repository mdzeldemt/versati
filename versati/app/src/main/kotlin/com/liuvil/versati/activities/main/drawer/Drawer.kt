package com.liuvil.versati.activities.main.drawer

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DrawerItem(
    val title: String,
    val icon: Icon? = null,
    val badge: String? = null,
    val selected: Boolean = false,
    val onClick: () -> Unit
) {
    sealed interface Icon {
        data object Loading: Icon
        data class Data(val bytes: String): Icon
        data class Vector(val vector: ImageVector): Icon
    }
}

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
                LazyColumn (
                    modifier = Modifier.padding(padding)
                ) {
                    items.forEach {
                        item {
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        it.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                icon = {
                                    it.icon?.let { icon ->
                                        Box(Modifier.size(20.dp)) {
                                            when (icon) {
                                                is DrawerItem.Icon.Loading ->
                                                    CircularProgressIndicator()
                                                is DrawerItem.Icon.Data -> {
                                                    val decodedBytes = Base64.decode(
                                                        icon.bytes.substringAfter(","),
                                                        Base64.DEFAULT
                                                    )
                                                    val bitmap = BitmapFactory.decodeByteArray(
                                                        decodedBytes,
                                                        0,
                                                        decodedBytes.size
                                                    )

                                                    if (bitmap != null) {
                                                        Image(
                                                            bitmap = bitmap.asImageBitmap(),
                                                            contentDescription = null,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                }
                                                is DrawerItem.Icon.Vector ->
                                                    Icon(
                                                        icon.vector,
                                                        contentDescription = null
                                                    )
                                            }
                                        }
                                    }
                                },
                                badge = {
                                    it.badge?.let { badge ->
                                        Text(badge)
                                    }
                                },
                                selected = it.selected,
                                onClick = it.onClick
                            )
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}
