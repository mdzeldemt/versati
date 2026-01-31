package com.liuvil.versati.components.drawer

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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val DRAWER_ICON_SIZE = 20.dp

@Composable
internal fun DrawerItemIcon(
    imageVector: ImageVector
) {
    Box(Modifier.size(DRAWER_ICON_SIZE)) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}

@Composable
internal fun DrawerItemIcon(
    imageBitmap: ImageBitmap
) {
    Box(Modifier.size(DRAWER_ICON_SIZE)) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
internal fun NavigationDrawerItemGroup(
    label: @Composable () -> Unit,
    selected: Boolean,
    expanded: Boolean,
    badge: @Composable () -> Unit,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column {
        NavigationDrawerItem(
            label = {
                label()
            },
            icon = {
                Icon(
                    imageVector =
                        if (expanded)
                            Icons.Default.KeyboardArrowDown
                        else
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        onClick = {
                            onToggle()
                        },
                        indication = null,
                        interactionSource = null
                    )
                )
            },
            badge = {
                badge()
            },
            selected = selected,
            onClick = onClick
        )

        if (expanded) {
            Column(
                Modifier.padding(start = 24.dp)
            ) {
                content()
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
