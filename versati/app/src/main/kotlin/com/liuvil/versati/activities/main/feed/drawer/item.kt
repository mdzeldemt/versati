package com.liuvil.versati.activities.main.feed.drawer

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

sealed class DrawerItem(
    open val title: String,
    open val badge: String?,
    open val selected: Boolean,
    open val onClick: () -> Unit
)

data class FlatDrawerItem(
    override val title: String,
    val icon: Icon? = null,
    override val badge: String? = null,
    override val selected: Boolean = false,
    override val onClick: () -> Unit
): DrawerItem(title, badge, selected, onClick) {
    sealed interface Icon {
        data class Data(val data: String): Icon
        data class Vector(val vector: ImageVector): Icon
    }
}

@Composable
internal fun FlatDrawerItem(
    item: FlatDrawerItem
) {
    NavigationDrawerItem(
        label = {
            DrawerItemLabel(text = item.title)
        },
        icon = {
            item.icon?.let { icon ->
                Box(Modifier.size(20.dp)) {
                    when (icon) {
                        is FlatDrawerItem.Icon.Data -> {
                            val decodedBytes = Base64.decode(
                                icon.data.substringAfter(","),
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
                        is FlatDrawerItem.Icon.Vector ->
                            Icon(
                                icon.vector,
                                contentDescription = null
                            )
                    }
                }
            }
        },
        badge = {
            item.badge?.let { badge ->
                DrawerItemBadge(text = badge)
            }
        },
        selected = item.selected,
        onClick = item.onClick
    )
}

@Composable
internal fun DrawerItem(
    item: DrawerItem
) {
    when (item) {
        is FlatDrawerItem -> FlatDrawerItem(item)
        is ExpandableDrawerItem -> ExpandableDrawerItem(item)
    }
}

@Composable
internal fun ExpandableDrawerItem(
    item: ExpandableDrawerItem
) {
    Column {
        NavigationDrawerItem(
            label = {
                DrawerItemLabel(text = item.title)
            },
            icon = {
                Icon(
                    imageVector =
                    if (item.expanded)
                        Icons.Default.KeyboardArrowDown
                    else
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        onClick = {
                            item.onToggle()
                        },
                        indication = null,
                        interactionSource = null
                    )
                )
            },
            badge = {
                item.badge?.let { badge ->
                    DrawerItemBadge(text = badge)
                }
            },
            selected = item.selected,
            onClick = item.onClick
        )

        if (item.expanded) {
            Column(
                Modifier.padding(start = 24.dp)
            ) {
                item.children.forEach { child ->
                    DrawerItem(child)
                }
            }
        }
    }
}

@Composable
internal fun DrawerItemLabel(
    text: String
) {
    Text(
        text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun DrawerItemBadge(
    text: String
) {
    Text(text)
}